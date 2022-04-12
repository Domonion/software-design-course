package actors

import search.SearchEngine

class BingActor : SearchEngineActor() {
    override val searchEngine = SearchEngine.BING
}