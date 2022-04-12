import lexer.Lexer
import visitor.CalcVisitor
import visitor.PrintVisitor
import visitor.TranslateVisitor
import java.io.PrintWriter

fun process(expression: String) {
    val pw = PrintWriter(System.out)
    val tokens = Lexer(expression).tokenize()
    val rpnTokens = TranslateVisitor().visit(tokens)

    pw.use {
        PrintVisitor(it).visit(tokens)
        PrintVisitor(it).visit(rpnTokens)
        it.println(CalcVisitor().visit(rpnTokens))
    }
}

fun main() {
    val expression = "(23 + 10) * 5 - 3 * (32 + 5) * (10 - 4 * 5) + 8 / 2"

    try {
        process(expression)
    } catch (e: IllegalStateException) {
        println(e.message)
        e.printStackTrace()
    }
}