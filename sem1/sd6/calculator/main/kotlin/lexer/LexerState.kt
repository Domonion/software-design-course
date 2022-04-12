package lexer

sealed class LexerState(protected val lexerTarget: MutableList<Token>) {
    protected abstract fun step(c: Char): Pair<LexerState, Token?>

    open fun processChar(c: Char): LexerState {
        val result = step(c)

        result.second?.let { lexerTarget.add(it) }

        return result.first
    }

    open fun processEof(): LexerState {
        return EofState(lexerTarget)
    }
}

class BaseState(lexerTarget: MutableList<Token>) : LexerState(lexerTarget) {
    override fun step(c: Char): Pair<LexerState, Token?> = when (c) {
        ' ' -> this to null
        '(' -> this to Left
        ')' -> this to Right
        '+' -> this to Plus
        '-' -> this to Minus
        '*' -> this to Times
        '/' -> this to Div
        in '0'..'9' -> NumberState(lexerTarget, c - '0') to null
        else -> ErrorState(lexerTarget, "Invalid symbol '$c' doesn't match any token") to null
    }
}

class NumberState(lexerTarget: MutableList<Token>, val number: Int) : LexerState(lexerTarget) {
    override fun step(c: Char): Pair<LexerState, Token?> = when {
        c in '1'..'9' && number == 0 -> ErrorState(lexerTarget, "Invalid number starting with '0'") to null
        c in '0'..'9' -> NumberState(lexerTarget, number * 10 + (c - '0')) to null
        else -> {
            lexerTarget.add(NumberToken(number))
            BaseState(lexerTarget).processChar(c) to null
        }
    }

    override fun processEof(): LexerState {
        lexerTarget.add(NumberToken(number))
        return super.processEof()
    }
}

class EofState(lexerTarget: MutableList<Token>) : LexerState(lexerTarget) {
    override fun step(c: Char): Pair<LexerState, Token?> {
        error("No characters should follow end of file")
    }
}

class ErrorState(lexerTarget: MutableList<Token>, private val error: String) : LexerState(lexerTarget) {
    val exception get() = IllegalStateException(error)

    override fun step(c: Char): Pair<LexerState, Token?> {
        return this to null
    }

    override fun processEof(): LexerState {
        return this
    }
}
