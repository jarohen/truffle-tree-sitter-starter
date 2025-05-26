plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    kapt(libs.truffle.dsl.processor)
    implementation(libs.graal.sdk)
    implementation(libs.truffle.api)
    implementation(libs.truffle.runtime)

    implementation(libs.jtreesitter)

    testImplementation(kotlin("test-junit"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

sourceSets {
    main {
        resources {
            srcDir(project(":tree-sitter").layout.buildDirectory.dir("lib"))
        }
    }
}

java {
    modularity.inferModulePath = true

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

tasks.named("processResources") {
    dependsOn(":tree-sitter:buildTreeSitter")
}

tasks.compileJava {
    options.compilerArgs.add("--module-path")
    options.compilerArgs.add(classpath.asPath)

    options.compilerArgs.add("--patch-module")
    options.compilerArgs.add("intlang.language=${sourceSets["main"].output.asPath}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    group = "build"
    archiveFileName.set("int-language.jar")

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
