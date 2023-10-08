package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.model.FirestoreUserEntity

interface FirestoreDomainRepository {
    suspend fun insertUser(authenticateUserEntity: AuthenticateUserEntity): Boolean
    suspend fun isUserExist(uid: String): Boolean
    suspend fun getUser(uid: String): FirestoreUserEntity
    suspend fun updateUsername(username: String, uid: String): Boolean
}