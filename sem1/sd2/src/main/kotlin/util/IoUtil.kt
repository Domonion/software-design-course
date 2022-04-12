package util

object IoUtil {
    fun <T> alwaysTrue(): (T) -> Boolean = { _ -> true }

    fun ask(text: String, check: (String) -> Boolean): String {
        while (true) {
            print(text)

            val result = readLine()

            if (result == null || !check(result))
                println("Incorrect input, try again")
            else
                return result
        }
    }
}