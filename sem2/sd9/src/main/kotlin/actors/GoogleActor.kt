package actors

import search.SearchEngine

class GoogleActor() : SearchEngineActor() {
    override val searchEngine = SearchEngine.GOOGLE
}