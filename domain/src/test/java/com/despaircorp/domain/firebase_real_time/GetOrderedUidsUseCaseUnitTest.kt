package com.despaircorp.domain.firebase_real_time

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.utils.TestCoroutineRule
import org.junit.Rule
import org.junit.Test

class GetOrderedUidsUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val useCase = GetOrderedUidsUseCase()
    
    companion object {
        private const val DEFAULT_UID_ONE = "DEFAULT_UID_ONE"
        private const val DEFAULT_UID_TWO = "DEFAULT_UID_TWO"
    }
    
    @Test
    fun `nominal case - return pair (if)`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID_ONE, DEFAULT_UID_TWO)
        
        assertThat(result).isEqualTo(Pair(DEFAULT_UID_ONE, DEFAULT_UID_TWO))
        
        
    }
    
    @Test
    fun `edge case - return other pair (else)`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID_TWO, DEFAULT_UID_ONE)
        
        assertThat(result).isEqualTo(Pair(DEFAULT_UID_ONE, DEFAULT_UID_TWO))
    }
}