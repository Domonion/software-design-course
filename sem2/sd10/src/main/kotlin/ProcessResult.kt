import io.netty.handler.codec.http.HttpResponseStatus
import rx.Observable

data class ProcessResult(val responseStatus: HttpResponseStatus, val response: Observable<String>)