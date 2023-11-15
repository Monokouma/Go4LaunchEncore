package com.despaircorp.domain.firebase_real_time

import android.util.Log
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllUserConversationUseCase @Inject constructor(
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(): Flow<List<ChatEntity>> =
        firebaseRealTimeDomainRepository.getAllLastChatEntities(
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
        ).map { lastChats ->
            Log.i("Monokouma", lastChats.toString())
            lastChats.sortedByDescending { it.timestamp }
        }
}