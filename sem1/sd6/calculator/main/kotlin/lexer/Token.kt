package lexer

import visitor.TokenVisitor

sealed class Token {
    abstract fun accept(visitor: TokenVisitor<*>)
}

sealed class OperationToken(private val representation: String) : Token() {
    override fun accept(visitor: TokenVisitor<*>) = visitor.visit(this)
    override fun toString(): String = representation
}

object Plus : OperationToken("PLUS")
object Minus : OperationToken("MINUS")
object Times : OperationToken("TIMES")
object Div : OperationToken("DIV")

data class NumberToken(val number: Int) : Token() {
    override fun accept(visitor: TokenVisitor<*>) = visitor.visit(this)
    override fun toString(): String = "<number($number)>"
}

sealed class ParenthesisToken(private val representation: String) : Token() {
    override fun accept(visitor: TokenVisitor<*>) = visitor.visit(this)
    override fun toString(): String = representation
}

object Left : ParenthesisToken("LP(")
object Right : ParenthesisToken(")RP")