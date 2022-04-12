package visitor

import lexer.NumberToken
import lexer.OperationToken
import lexer.ParenthesisToken
import lexer.Token
import java.io.PrintWriter

class PrintVisitor(private val output: PrintWriter) : TokenVisitor<Unit> {
    override fun visit(token: NumberToken) = output.print(token)
    override fun visit(token: ParenthesisToken) = output.print(token)
    override fun visit(token: OperationToken) = output.print(token)

    override fun visit(tokens: Collection<Token>) {
        tokens.forEachIndexed { i, token ->
            token.accept(this)

            if (i < tokens.size - 1) {
                output.print(' ')
            }
        }
        output.println()
    }
}