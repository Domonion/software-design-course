import api.ApiQuery
import api.StandardQuery
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.isA
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import vk.IVkEngine
import vk.IVkQueryTranslator
import vk.VkApi
import vk.VkTranslator
import java.lang.IllegalArgumentException

class VkApiTests {

    @Test
    fun simpleVkApiTest(): Unit = runBlocking {
        val translatorMock = Mockito.mock(IVkQueryTranslator::class.java)
        val engineMock = Mockito.mock(IVkEngine::class.java)

        whenever(translatorMock.translateQuery(isA())).thenAnswer {
            val standardQuery = it.arguments.single() as StandardQuery

            ApiQuery(standardQuery.queryName, standardQuery.parameters)
        }

        whenever(engineMock.send(isA())).thenAnswer {
            val apiQuery = it.arguments.single() as ApiQuery

            Json.encodeToJsonElement(apiQuery)
        }

        val api = VkApi(translatorMock, engineMock)
        val simpleQuery = StandardQuery("a", mapOf("b" to "c"))
        val expectedApiQuery = ApiQuery("a", simpleQuery.parameters)
        val expectedResult = Json.encodeToJsonElement(expectedApiQuery)
        val actualResult = api.query(simpleQuery)

        Assertions.assertEquals(expectedResult, actualResult)
        verify(translatorMock).translateQuery(simpleQuery)
        verify(engineMock).send(expectedApiQuery)
    }

    @Test
    fun simpleVkTranslatorTest() {
        val translator = VkTranslator()
        val goodQuery = StandardQuery("hashtag", mapOf("a" to "b", "c" to "d"))
        val badQuery = StandardQuery("3u0732fhhg4879", mapOf())
        val goodExpected = ApiQuery("newsfeed.search", goodQuery.parameters)
        val goodActual = translator.translateQuery(goodQuery)

        Assertions.assertEquals(goodActual, goodExpected)
        Assertions.assertThrows(IllegalArgumentException::class.java) { translator.translateQuery(badQuery) }
    }
}