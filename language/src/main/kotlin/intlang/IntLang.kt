package intlang

import intlang.Reader.Companion.readForms
import com.oracle.truffle.api.CallTarget
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy
import com.oracle.truffle.api.TruffleLanguage.Registration
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.RootNode

@Registration(
    id = "intlang",
    name = "IntLang",
    defaultMimeType = "text/int-lang",
    characterMimeTypes = ["text/int-lang"],
    contextPolicy = ContextPolicy.EXCLUSIVE
)
class IntLang : TruffleLanguage<IntLang.Context>() {

    class Context

    override fun createContext(env: Env) = Context()

    override fun parse(request: ParsingRequest): CallTarget {
        val nodes = request.source.readForms().map(::emitForm)

        return object : RootNode(this) {
            override fun execute(frame: VirtualFrame): Any? {
                var res: Long? = null
                for (node in nodes) {
                    res = node.execute(frame)
                }
                return res
            }

        }.callTarget
    }
}