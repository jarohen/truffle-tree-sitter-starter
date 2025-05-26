package intlang

import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.source.SourceSection

abstract class IntLangNode : Node() {
    abstract fun execute(frame: VirtualFrame): Long
}

class IntNode(private val value: Long, private val loc: SourceSection?) : IntLangNode() {
    override fun execute(frame: VirtualFrame) = value
    override fun getSourceSection() = loc
}

fun emitForm(form: Form): IntLangNode = when (form) {
    is IntForm -> IntNode(form.value, form.loc)
}
