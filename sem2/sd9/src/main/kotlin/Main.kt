//package com.domonion.patterns.homework.actors
//
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.channels.toList
//import search.QueryResult
//import search.SearchRequest
//import util.runSearchWith
//import kotlin.time.Duration.Companion.seconds
//
//suspend fun printResult(result: Channel<QueryResult>) {
//    for ((from, sites) in result.toList().sortedBy { it.from }) {
//        println("$from: $sites")
//    }
//}
//
//fun ask(text: String, check: (String) -> Boolean): String {
//    while (true) {
//        print(text)
//
//        val result = readLine()
//
//        if (result == null || !check(result))
//            println("Incorrect input, try again")
//        else
//            return result
//    }
//}
//suspend fun main() {
//    while (true) {
//        println("To quit - type `q!`.")
//
//        val query = ask("Search query: ") { true }
//
//        if (query == "q!")
//            break
//
//        val timeout = ask("Timeout in seconds: ") { it == "q!" || it.toIntOrNull() != null }
//
//        if (timeout == "q!")
//            break
//
//        val duration = timeout.toInt().seconds
//        val request = SearchRequest(query, duration)
//
//        runSearchWith(request, ::printResult)
//    }
//}