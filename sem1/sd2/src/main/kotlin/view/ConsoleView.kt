package view

class ConsoleView(private val draw: () -> String): IView {
    override fun show() = println(draw())
}