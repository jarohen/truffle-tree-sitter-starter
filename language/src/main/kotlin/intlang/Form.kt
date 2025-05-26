package intlang

import com.oracle.truffle.api.source.SourceSection

sealed interface Form {
    val loc: SourceSection
    override fun toString(): String
}

class IntForm(val value: Long, override val loc: SourceSection) : Form {
    override fun toString(): String = value.toString()
}
