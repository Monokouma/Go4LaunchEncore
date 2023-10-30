package com.despaircorp.data.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume

suspend fun <T> Task<T>.safeAwait(): T? {
    // fast path
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException("Task $this was cancelled normally.")
            } else {
                result as T
            }
        } else {
            e.printStackTrace()
            null
        }
    }

    return suspendCancellableCoroutine { cont ->
        // Run the callback directly to avoid unnecessarily scheduling on the main thread.
        addOnCompleteListener(DirectExecutor) {
            val e = it.exception
            if (e == null) {
                if (it.isCanceled) {
                    cont.cancel()
                } else {
                    cont.resume(it.result as T)
                }
            } else {
                e.printStackTrace()
                cont.resume(null)
            }
        }
    }
}

private object DirectExecutor : Executor {
    override fun execute(r: Runnable) {
        r.run()
    }
}
