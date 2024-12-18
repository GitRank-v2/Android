package com.dragonguard.android.util

import android.util.Log
import com.dragonguard.android.GitRankApplication
import com.dragonguard.android.data.model.StandardError
import retrofit2.Response
import retrofit2.Retrofit

suspend fun <T : Any, R : Any> handleApi(
    execute: suspend () -> Response<T>,
    mapper: (T) -> R,
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
            //Log.d("error", response.errorBody()!!.string())
            getFailDataResult(body, response)
        }
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}

suspend fun <T : Any, R : Any> handleApi(
    execute: suspend () -> Response<T>,
    retrofit: Retrofit,
    mapper: (T) -> R,
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
            response.errorBody()?.let { it ->
                Log.d("error", "exist")
                val errorBody = retrofit.responseBodyConverter<StandardError>(
                    StandardError::class.java,
                    StandardError::class.java.annotations
                ).convert(it)
                Log.d("error", errorBody.toString())
                errorBody?.let { error ->
                    Log.d("error", error.data.data.error_id)
                    Log.d("error", error.code.toString())
                    Log.d("error", error.message)
                    DataResult.Fail(statusCode = error.code, message = error.data.message)
                }
            } ?: run {
                Log.d("error", response.message())
                getFailDataResult(body, response)
            }
        }
    } catch (e: Exception) {
        Log.d("error", e.message.toString())
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
            DataResult.Success(false)
        } else {
            getFailDataResult(body, response)
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