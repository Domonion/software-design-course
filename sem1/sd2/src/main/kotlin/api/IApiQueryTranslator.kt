package api

interface IApiQueryTranslator {
    fun translateQuery(query: StandardQuery): ApiQuery
}