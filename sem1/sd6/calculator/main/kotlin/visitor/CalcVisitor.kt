package visitor

import lexer.*
import java.util.*

class CalcVisitor : TokenVisitor<Int> {
    companion object {
        private fun operation(operationToken: OperationToken): (Int, Int) -> Int = when (operationToken) {
            Plus -> Int::plus
            Minus -> { a, b -> b - a }
            Times -> Int::times
            Div -> { a, b -> b / a }
        }
    }

    private val stack: Stack<Int> = Stack()

    override fun visit(token: OperationToken) {
        check(stack.size >= 2) { "Stack doesn't have enough operands" }
        stack.push(operation(token)(stack.pop(), stack.pop()))
    }

    override fun visit(token: NumberToken) {
        stack.push(token.number)
    }

    override fun visit(token: ParenthesisToken) = error("Unsupported token '$token'")

    override fun visit(tokens: Collection<Token>): Int {
        tokens.forEach {
            it.accept(this)
        }
        check(stack.size == 1) { "Invalid stack size ${stack.size}" }

        return stack.pop()
    }
}