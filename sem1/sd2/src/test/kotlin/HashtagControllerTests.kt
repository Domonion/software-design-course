import Util.validJsonString
import api.IApi
import controller.HashtagController
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Duration
import java.time.Instant

class HashtagControllerTests {
    @Test
    fun simpleHashControllerTest(): Unit = runBlocking {
        val api = Mockito.mock(IApi::class.java)
        val jsonElement = Json.parseToJsonElement(validJsonString(Instant.now().epochSecond - 3500))
        val hashtag = "#spb"
        val hours = Duration.ofHours(12)
        val hashtagController = HashtagController(api)

        whenever(api.query(any())).thenReturn(jsonElement)

        val result = hashtagController.countHashtag(hashtag, hours)

        Assertions.assertTrue(result.last() > 0 )
        verify(api).query(any())
    }
}