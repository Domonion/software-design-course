import Util.validJsonString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import model.HashtagResponseModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.lang.IllegalArgumentException
import java.time.Instant

class ModelsTests {
    @Test
    fun hashtagModelTest() {
        val time = Instant.now().epochSecond - 3500
        val goodJson: JsonElement = Json.parseToJsonElement(validJsonString(time))

        val badJson: JsonElement = Json.parseToJsonElement("{}")
        println(
            HashtagResponseModel(goodJson).entries().map { it.id() to it.date() }.joinToString()
        )

        HashtagResponseModel(goodJson).entries().map { it.id() to it.date() }.joinToString()
        Assertions.assertAll(
            Executable {
                Assertions.assertEquals(
                    HashtagResponseModel(goodJson).entries().map { it.id() to it.date() }.joinToString(),
                    "(43, $time), (18531, $time), (7463, $time)"
                )
            },
            Executable {
                Assertions.assertThrows(IllegalArgumentException::class.java) {
                    HashtagResponseModel(JsonNull).entries().map { it.id() to it.date() }.joinToString()
                }
            },
            Executable {
                Assertions.assertThrows(NullPointerException::class.java) {
                    HashtagResponseModel(badJson).entries().map { it.id() to it.date() }.joinToString()
                }
            }
        )
    }
}