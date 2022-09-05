package cu.lidev.core.common.network

import android.util.Log
import cu.lidev.core.R
import retrofit2.Response


interface RequestHandler {

    /**
     * Convert a Retrofit Response into the sealed class [ResponseResult] to handle more easily the API response objects.
     * @param call Retrofit suspend call.
     * @return A [ResponseResult] object wrapping the network response.
     */
    suspend fun <T : Any?> safeApiCall(call: suspend () -> Response<T>): ResponseResult<T>

}

/**
 * Custom [RequestHandler] implementation.
 */
class RequestHandlerImpl : RequestHandler {

    override suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ResponseResult<T> =
        try {
            val response = call.invoke()
            Log.d("TAG", "${response.body()}")
            if (response.isSuccessful) {
                ResponseResult.Success(response.body())
            } else {
                Log.d("TAG", "${response.errorBody()}")
                ResponseResult.Error(
                    exception = Pair(
                        first = response.code(),
                        second = R.string.http_error_default
                    )
                )
            }
        } catch (e: Exception) {
            ResponseResult.Error.DEFAULT
        }

}

/**
 * Handler for network request states.
 */
sealed class ResponseResult<out T : Any?> {
    data class Success<out T : Any?>(val data: T? = null) : ResponseResult<T>()
    data class Error(val exception: Pair<Int, Int>) : ResponseResult<Nothing>() {
        companion object {
            val DEFAULT = Error(exception = -1 to R.string.http_error_default)
        }
    }
}

