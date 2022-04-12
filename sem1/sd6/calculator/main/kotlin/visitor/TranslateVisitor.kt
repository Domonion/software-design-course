package visitor

import lexer.*
import java.util.*

class TranslateVisitor : TokenVisitor<List<Token>> {
    companion object {
        fun precedence(operationToken: OperationToken): Int = when (operationToken) {
            is Plus -> 0
            is Minus -> 0
            is Times -> 1
            is Div -> 1
        }
    }

    private val answerQueue: Queue<Token> = ArrayDeque()
    private val operationStack: Stack<Token> = Stack()

    private fun transfer(checkValid: Boolean = false) {
        if (checkValid) {
            check(operationStack.peek() is OperationToken) { "Unmatched opening parenthesis" }
        }

        answerQueue.add(operationStack.pop())
    }

    override fun visit(token: OperationToken) {
        while (operationStack.isNotEmpty() && operationStack.peek() is OperationToken) {
            val previousToken = operationStack.peek() as OperationToken

            if (precedence(previousToken) >= precedence(token)) {
                transfer()
            } else {
                break
            }
        }

        operationStack.push(token)
    }

    override fun visit(token: NumberToken) {
        answerQueue.add(token)
    }

    override fun visit(token: ParenthesisToken) {
        when (token) {
            is Left -> operationStack.push(token)
            is Right -> {
                while (operationStack.isNotEmpty() && operationStack.peek() is OperationToken) {
                    transfer()
                }

                check(operationStack.isNotEmpty() && operationStack.pop() is Left) { "Unmatched right parenthesis" }
            }
        }
    }

    override fun visit(tokens: Collection<Token>): List<Token> {
        tokens.forEach { token ->
            token.accept(this)
        }

        while (operationStack.isNotEmpty()) {
            transfer(true)
        }

        return answerQueue.toList()
    }
}