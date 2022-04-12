package lexer

import java.util.*

class Lexer(private val s: String) {
    private val tokens: MutableList<Token> = mutableListOf()
    private var state: LexerState = BaseState(tokens)

    fun tokenize(): List<Token> {
        tokens.clear()

        for (c in s) {
            state = state.processChar(c)
        }

        state = state.processEof()

        return when (val st = state) {
            is ErrorState -> throw st.exception
            is EofState -> Collections.unmodifiableList(tokens)
            else -> throw IllegalStateException("Invalid state on tokens request")
        }
    }
}