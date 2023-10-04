package com.despaircorp.domain.firebaseAuth

import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import com.facebook.AccessToken

interface FirebaseAuthDomainRepository {
    suspend fun isUserAlreadyAuth(): Boolean
    suspend fun isUserWithCredentialsExist(mail: String, password: String): Boolean
    suspend fun createUserWithCredentials(
        mail: String,
        password: String
    ): Boolean
    
    suspend fun getCurrentAuthenticatedUser(): AuthenticateUserEntity
    suspend fun signInTokenUser(token: AccessToken): Boolean
}