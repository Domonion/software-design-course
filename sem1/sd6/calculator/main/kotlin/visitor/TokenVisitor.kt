package visitor

import lexer.NumberToken
import lexer.OperationToken
import lexer.ParenthesisToken
import lexer.Token

interface TokenVisitor<T> {
    fun visit(token: OperationToken)
    fun visit(token: NumberToken)
    fun visit(token: ParenthesisToken)
    fun visit(tokens: Collection<Token>): T
}