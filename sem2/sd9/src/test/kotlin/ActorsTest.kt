@file:OptIn(ExperimentalTime::class)

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import search.QueryResult
import search.SearchEngine
import search.SearchRequest
import util.groupUse
import util.runSearchWith
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActorsTest {
    companion object {
        private val responses1 = Responses(mapOf("simple" to listOf("site#1")))
        private val responses2 = Responses(mapOf("simple" to listOf("site#2"), "empty" to emptyList()))
        private val responses3 = Responses(mapOf("simple" to listOf("site#3", "site#4"), "only" to listOf("anything")))
        private val testSuites = listOf(responses1, responses2, responses3)
        private val responseFiles = listOf("responses1.txt", "responses2.txt", "responses3.txt")
        private val testSet = SearchEngine.values().zip(responseFiles).toMap()
    }

    @BeforeAll
    fun createTestSuites() {
        testSuites.forEachIndexed { index, responses ->
            Files.writeString(Path.of("responses${index + 1}.txt"), Json.encodeToString(responses))
        }
    }

    private fun doTest(
        requestTimeout: Duration,
        serverTimeout: Duration,
        requestName: String,
        servers: Array<SearchEngine>,
        check: suspend (Channel<QueryResult>) -> Unit
    ) {
        groupUse(*servers.map { StubServer(it.port, serverTimeout, testSet[it]!!, it) }.toTypedArray()) {
            val request = SearchRequest(requestName, requestTimeout)

            runBlocking {
                runSearchWith(request, check)
            }
        }
    }

    @Test
    fun oneEngineTest() {
        val requestName = "simple"
        val correctAnswer = responses1.answers[requestName]!!

        doTest(1.seconds, Duration.ZERO, "simple", arrayOf(SearchEngine.GOOGLE)) { channel ->
            val actual = channel.toList().single().sites

            assertTrue(actual == correctAnswer)
        }
    }

    @Test
    fun allEngineTests() {
        val requestName = "simple"
        val correctAnswer1 = responses1.answers[requestName]!!.toSet()
        val correctAnswer2 = responses2.answers[requestName]!!.toSet()
        val correctAnswer3 = responses3.answers[requestName]!!.toSet()
        val correct = correctAnswer1.union(correctAnswer2).union(correctAnswer3)

        doTest(1.seconds, Duration.ZERO, requestName, SearchEngine.values()) { channel ->
            val actual = channel.toList().flatMap { it.sites }.toSet()

            assertTrue(actual == correct)
        }
    }

    @Test
    fun emptyTest() {
        val requestName = "empty"
        val correct = responses2.answers[requestName]!!.toSet()

        doTest(1.seconds, Duration.ZERO, requestName, SearchEngine.values()) { channel ->
            val actual = channel.toList().flatMap { it.sites }.toSet()

            assertTrue(actual == correct)
        }
    }

    @Test
    fun onlyTest() {
        val requestName = "only"
        val correct = responses3.answers[requestName]!!.toSet()

        doTest(1.seconds, Duration.ZERO, requestName, SearchEngine.values()) { channel ->
            val actual = channel.toList().flatMap { it.sites }.toSet()

            assertTrue(actual == correct)
        }
    }

    @Test
    fun incorrectTest() {
        val requestName = "abracadabra"
        val correct = emptySet<String>()

        doTest(1.seconds, Duration.ZERO, requestName, SearchEngine.values()) { channel ->
            val actual = channel.toList().flatMap { it.sites }.toSet()

            assertTrue(actual == correct)
        }
    }

    @Test
    fun timeoutTest1() {
        val requestName = "simple"

        doTest(1.seconds, 3.seconds, requestName, SearchEngine.values()) { channel ->
            assertTrue(channel.toList().isEmpty())
        }
    }
}