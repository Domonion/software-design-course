import controller.VkCredentialsController
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import view.ConsoleView
import java.io.*

class ViewTest {
    lateinit var currentProgramOutput: InputStream

    @BeforeEach
    fun createStreams() {
        val output = PipedOutputStream()
        val input = PipedInputStream(output)

        System.setOut(PrintStream(output))
        currentProgramOutput = input
    }

    @ParameterizedTest
    @MethodSource("textArguments")
    fun consoleViewTest(text: String) {
        ConsoleView { text }.show()
        System.out.close()
        Assertions.assertEquals(text, IOUtils.toString(currentProgramOutput, "utf-8").trim())
    }

    companion object {
        @JvmStatic
        fun textArguments() = listOf(
            "text",
            "text with spaces",
            "text\nwith\nbreaks"
        )
    }

    @AfterEach
    fun closeStreams() {
        System.out.close()
        currentProgramOutput.close()
    }
}