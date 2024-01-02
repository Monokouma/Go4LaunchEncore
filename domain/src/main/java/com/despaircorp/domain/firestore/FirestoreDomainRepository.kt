package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import kotlinx.coroutines.flow.Flow

interface FirestoreDomainRepository {
    suspend fun insertUser(authenticateUserEntity: AuthenticateUserEntity): Boolean
    suspend fun isUserExist(uid: String): Boolean
    suspend fun getUser(uid: String): FirestoreUserEntity
    suspend fun updateUsername(username: String, uid: String): Boolean
    fun getUserAsFlow(uid: String): Flow<FirestoreUserEntity>
    suspend fun updateMailAddress(uid: String, newMailAddress: String): Boolean
    suspend fun updateUserImage(uid: String, pictureUrl: String): Boolean
    fun getAllFirestoreUsersAsFlow(): Flow<List<FirestoreUserEntity>>
    
    suspend fun updateUserPresence(uid: String, isPresent: Boolean)
    suspend fun updateCurrentEatingRestaurant(placeId: String, uid: String): Boolean
    fun getCurrentEatingRestaurantForAuthenticatedUser(uid: String): Flow<String?>
    suspend fun removeCurrentEatingRestaurant(uid: String): Boolean
    suspend fun addCurrentEatingRestaurant(placeId: String, uid: String): Boolean
    
}