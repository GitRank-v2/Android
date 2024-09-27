package com.dragonguard.android.util

import com.dragonguard.android.GitRankApplication
import retrofit2.Response

suspend fun <T : Any, R : Any> handleApi(
    execute: suspend () -> Response<T>,
    mapper: (T) -> R
): DataResult<R> {
    if (GitRankApplication.isOnline().not()) {
        return DataResult.Error(Exception(NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful) {
            body?.let {
                DataResult.Success(mapper(it))
            } ?: run {
                throw NullPointerException(NETWORK_EXCEPTION_BODY_IS_NULL)
            }
        } else {
            getFailDataResult(body, response)
        }
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}

suspend fun <T : Any> handleAdminApi(
    execute: suspend () -> Response<T>
): DataResult<Boolean> {
    if (GitRankApplication.isOnline().not()) {
        return DataResult.Error(Exception(NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful) {
            body?.let {
                DataResult.Success(true)
            } ?: run {
                throw NullPointerException(NETWORK_EXCEPTION_BODY_IS_NULL)
            }
        } else if (response.code() == 403) {
            DataResult.Success(true)
        } else {
            getFailDataResult(body, response)
        }
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}

suspend fun <T : Any> handleLoginApi(
    execute: suspend () -> Response<T>,
): DataResult<Boolean?> {
    if (GitRankApplication.isOnline().not()) {
        return DataResult.Error(Exception(NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        val response = execute()
        val body = response.body()
        if (response.code() == 200) {
            return DataResult.Success(true)
        } else {
            return DataResult.Success(null)
        }
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}

suspend fun <T : Any> handleWithdrawApi(
    execute: suspend () -> Response<T>,
): DataResult<Boolean> {
    if (GitRankApplication.isOnline().not()) {
        return DataResult.Error(Exception(NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        val response = execute()
        val body = response.body()
        if (response.code() == 204) {
            return DataResult.Success(true)
        } else {
            return DataResult.Success(false)
        }
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}

private const val NETWORK_EXCEPTION_OFFLINE_CASE = "network status is offline"
private const val NETWORK_EXCEPTION_BODY_IS_NULL = "result body is null"

private fun <T : Any> getFailDataResult(body: T?, response: Response<T>) = body?.let {
    DataResult.Fail(statusCode = response.code(), message = it.toString())
} ?: run {
    DataResult.Fail(statusCode = response.code(), message = response.message())
}