package controller

import util.IoUtil.ask
import view.ConsoleView
import java.lang.StringBuilder
import java.time.Duration

class MainController(private val hashtagController: HashtagController) : IController {
    fun execute() {
        println("To quit - type `q!`. Hashtag should start with `#`.")

        while (true) {
            val hashtag = ask("Hashtag: ") { it.startsWith('#') || it == "q!" }

            if (hashtag == "q!")
                return

            val hours = ask("Last hours([1..24]): ") { it == "q!" || it.toIntOrNull() != null && it.toInt() in 1..24 }

            if (hours == "q!")
                return

            val distribution = hashtagController.countHashtag(hashtag, Duration.ofHours(hours.toLong()))
            val view = ConsoleView {
                val result = StringBuilder()

                distribution.forEachIndexed { index, i ->
                    result.append("At hour ${index + 1} = $i\n")
                }

                result.toString()
            }

            view.show()
        }
    }
}