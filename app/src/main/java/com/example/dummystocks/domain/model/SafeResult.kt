package com.example.dummystocks.domain.model

/**
 * Wrapper for Success/Failure data for any operation.
 */
sealed class SafeResult<out T> {
  data class Success<out T>(val data: T, val message: String = "") : SafeResult<T>()
  data class Failure(val throwable: Throwable) : SafeResult<Nothing>()

  /**
   * Get result unsafely.
   * @return [Success.data] if this is [Success] else,
   * @throws [Failure.throwable] if this [Failure].
   */
  fun result(): T {
    when (this) {
      is Success -> return data
      is Failure -> throw throwable
    }
  }

  /**
   * Get result safely, ignore failure.
   * @return [Success.data] if this is [Success] else `null`.
   */
  fun resultIgnoreException(): T? {
    return when (this) {
      is Success -> data
      is Failure -> null
    }
  }

  /**
   * Perform mapping of safe result if success.
   * @return new [SafeResult] of type [U].
   */
  suspend fun <U> ifSuccess(block: suspend (Success<T>) -> SafeResult<U>): SafeResult<U> {
    return when (this) {
      is Success -> block(this)
      is Failure -> this
    }
  }

  /**
   * Perform mapping of safe result if success.
   * @return new [SafeResult] of type [U].
   */
  suspend fun <U> ifSuccessData(block: suspend (T) -> SafeResult<U>): SafeResult<U> {
    return when (this) {
      is Success -> block(this.data)
      is Failure -> this
    }
  }

  /**
   * Perform mapping of safe result if success.
   * @return new [SafeResult] of type [U].
   */
  suspend fun <U> mapSuccessData(block: suspend (T) -> U): SafeResult<U> {
    return when (this) {
      is Success -> Success(block(this.data))
      is Failure -> this
    }
  }

}

suspend fun <T> safeResult(block: suspend () -> T): SafeResult<T> {
  return try {
    val result = block()
    SafeResult.Success(result)
  } catch (e: Exception) {
    SafeResult.Failure(e)
  }
}