package com.hasib.callerid.data

import kotlinx.coroutines.CancellationException
import com.hasib.callerid.data.model.Result

suspend fun <T> handleDataFetch(apiCall: suspend () -> T): Result<T> {
    return try {
        val response = apiCall()
        Result.Success(response)
    } catch (e: Throwable) {
        if (e is CancellationException) {
            throw e
        }
        Result.Error(e)
    }
}
