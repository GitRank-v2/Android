package com.dragonguard.android.util

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Fail(val statusCode: Int, val message: String) : DataResult<Nothing>()
    data class Error(val exception: Exception) : DataResult<Nothing>()
}

inline fun <T> DataResult<T>.onSuccess(action: (T) -> Unit): DataResult<T> {
    if (this is DataResult.Success) {
        action(data)
    }
    return this
}

inline fun <T> DataResult<T>.onFail(resultCode: (Int) -> Unit): DataResult<T> {
    if (this is DataResult.Fail) {
        resultCode(this.statusCode)
    }
    return this
}

inline fun <T> DataResult<T>.onError(action: (Exception) -> Unit): DataResult<T> {
    if (this is DataResult.Fail) {
        action(IllegalArgumentException("code : ${this.statusCode}, message : ${this.message}"))
    } else if (this is DataResult.Error) {
        action(this.exception)
    }
    return this
}

inline fun <T> DataResult<T>.onException(action: (Exception) -> Unit): DataResult<T> {
    if (this is DataResult.Error) {
        action(this.exception)
    }
    return this
}