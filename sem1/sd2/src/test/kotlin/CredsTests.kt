import controller.VkCredentialsController
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

class CredsTests {
    lateinit var currentProgramInput: OutputStream

    @BeforeEach
    fun createStreams() {
        val output = PipedOutputStream()
        val input = PipedInputStream(output)

        System.setIn(input)
        currentProgramInput = output
    }

    @ParameterizedTest
    @MethodSource("textArguments")
    fun vkCredentialsTest(text: String) {
        IOUtils.write(text, currentProgramInput, "utf-8")
        currentProgramInput.close()
        val creds = VkCredentialsController()

        if (text.contains("\n"))
            Assertions.assertNotEquals(text, creds.accessToken)
        else
            Assertions.assertEquals(text, creds.accessToken)
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
        currentProgramInput.close()
    }
}