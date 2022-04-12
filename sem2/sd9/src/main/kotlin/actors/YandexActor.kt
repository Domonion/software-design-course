package actors

import search.SearchEngine

class YandexActor : SearchEngineActor() {
    override val searchEngine = SearchEngine.YANDEX
}