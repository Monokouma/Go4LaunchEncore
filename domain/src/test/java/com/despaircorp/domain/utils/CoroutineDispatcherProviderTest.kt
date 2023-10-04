package com.despaircorp.domain.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.Dispatchers
import org.junit.Test

class CoroutineDispatcherProviderTest {
    @Test
    fun `verify Dispatchers_Main`() {
        assertThat(CoroutineDispatcherProvider().main).isEqualTo(Dispatchers.Main)
    }
    
    @Test
    fun `verify Dispatchers_IO`() {
        assertThat(CoroutineDispatcherProvider().io).isEqualTo(Dispatchers.IO)
    }
}