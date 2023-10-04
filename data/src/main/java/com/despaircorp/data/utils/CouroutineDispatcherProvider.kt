package com.despaircorp.data.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoroutineDispatcherProvider @Inject constructor() {
    val main: CoroutineDispatcher = Dispatchers.Main
    val mainImmediate: CoroutineDispatcher = Dispatchers.Main.immediate
    val io: CoroutineDispatcher = Dispatchers.IO
}