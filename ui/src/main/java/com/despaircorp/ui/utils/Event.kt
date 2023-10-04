package com.despaircorp.ui.utils

import androidx.annotation.VisibleForTesting

open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? = if (hasBeenHandled) {
        null
    } else {
        hasBeenHandled = true
        content
    }

    @VisibleForTesting
    fun peekContent(): T = content
}