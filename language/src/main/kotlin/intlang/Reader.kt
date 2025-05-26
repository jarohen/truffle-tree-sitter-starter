package intlang

import com.oracle.truffle.api.source.Source
import io.github.treesitter.jtreesitter.Language
import io.github.treesitter.jtreesitter.Node
import io.github.treesitter.jtreesitter.Parser
import java.io.File.createTempFile
import java.lang.foreign.Arena
import java.lang.foreign.SymbolLookup

class Reader(private val src: Source) {
    companion object {
        private const val LIB_NAME = "tree_sitter_intlang"

        private fun libPath(): String? {
            val osName = System.getProperty("os.name")!!.lowercase()
            val archName = System.getProperty("os.arch")!!.lowercase()
            val ext: String
            val os: String
            val prefix: String
            when {
                "windows" in osName -> {
                    ext = "dll"
                    os = "windows"
                    prefix = ""
                }

                "linux" in osName -> {
                    ext = "so"
                    os = "linux"
                    prefix = "lib"
                }

                "mac" in osName -> {
                    ext = "dylib"
                    os = "macos"
                    prefix = "lib"
                }

                else -> {
                    throw UnsupportedOperationException("Unsupported operating system: $osName")
                }
            }
            val arch = when {
                "amd64" in archName || "x86_64" in archName -> "x64"
                "aarch64" in archName || "arm64" in archName -> "aarch64"
                else -> throw UnsupportedOperationException("Unsupported architecture: $archName")
            }
            val libPath = "/native/$os/$arch/$prefix$LIB_NAME.$ext"
            val libUrl = Reader::class.java.getResource(libPath) ?: return null
            return createTempFile(prefix + LIB_NAME, ".$ext").apply {
                writeBytes(libUrl.openStream().use { it.readAllBytes() })
                deleteOnExit()
            }.path
        }

        private val lang: Language =
            Language.load(SymbolLookup.libraryLookup(libPath(), Arena.global()), LIB_NAME)

        fun Source.readForms() = Reader(this).readForms()
    }

    fun readForms(): Sequence<Form> =
        Parser(lang)
            .parse(src.characters.toString())
            .orElseThrow()
            .rootNode.children.asSequence()
            .map { it.readForm() }

    private fun Node.readForm(): Form {
        if (isError) error("error reading node: $text")

        val loc = src.createSection(range.startByte, range.endByte - range.startByte())

        return when(type) {
            "int" -> IntForm(text!!.toLong(), loc)

            else -> error("unknown node type: $type")
        }
    }
}



