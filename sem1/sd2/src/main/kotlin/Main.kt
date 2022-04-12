import controller.MainController
import org.kodein.di.direct
import org.kodein.di.instance

fun main() {
    val mainController = Application.kodein.direct.instance<MainController>()

    mainController.execute()
}