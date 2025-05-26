package intlang

import org.graalvm.polyglot.Context
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IntLanguageTest {
    @Test
    fun testInt() {
        Context.newBuilder()
            .logHandler(System.err)
            .build()
            .use { ctx ->
                try {
                    ctx.enter()
                    assertEquals(42, ctx.eval("intlang", "42").asLong())
                } finally {
                    ctx.leave()
                }
            }
    }
}