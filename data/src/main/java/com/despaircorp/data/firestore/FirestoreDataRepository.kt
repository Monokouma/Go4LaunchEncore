package com.despaircorp.data.firestore

import com.despaircorp.data.firestore.dto.FirestoreUserDto
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class FirestoreDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val firestore: FirebaseFirestore,
) : FirestoreDomainRepository {
    override suspend fun insertUser(authenticateUserEntity: AuthenticateUserEntity): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                firestore
                    .collection("users")
                    .document(authenticateUserEntity.uid)
                    .set(authenticateUserEntity)
                    .addOnCompleteListener {
                        if (it.exception == null) {
                            cont.resume(it.isSuccessful)
                        } else {
                            cont.resume(false)
                        }
                    }
            }
        }
    
    override suspend fun isUserExist(uid: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                firestore.collection("users").document(uid).get().addOnCompleteListener {
                    if (it.exception == null) {
                        cont.resume(it.result.exists())
                    } else {
                        cont.resume(false)
                    }
                }
            }
        }
    
    override suspend fun getUser(uid: String): FirestoreUserEntity =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                firestore.collection("users").document(uid).addSnapshotListener { value, _ ->
                    val firestoreUserDto = value?.toObject<FirestoreUserDto>()
                    if (cont.isActive) {
                        cont.resume(
                            FirestoreUserEntity(
                                picture = firestoreUserDto?.picture
                                    ?: "https://w0.peakpx.com/wallpaper/733/998/HD-wallpaper-hedgedog-on-cloth-in-blur-green-bokeh-background-animals.jpg",
                                displayName = firestoreUserDto?.displayName ?: "none",
                                mailAddress = firestoreUserDto?.mailAddress
                                    ?: return@addSnapshotListener,
                                uid = firestoreUserDto.uid ?: return@addSnapshotListener,
                                currentlyEating = firestoreUserDto.currentlyEating ?: false,
                                eatingPlaceId = firestoreUserDto.eatingPlaceId,
                                online = firestoreUserDto.online ?: false
                            
                            )
                        )
                    }
                }
            }
        }
    
    override suspend fun updateUsername(username: String, uid: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            try {
                firestore
                    .collection("users")
                    .document(uid)
                    .update("displayName", username)
                    .await()
                true
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                e.printStackTrace()
                false
            }
        }
    
    override fun getUserAsFlow(uid: String): Flow<FirestoreUserEntity> = callbackFlow {
        val registration = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { documentSnapshot, _ ->
                val userDto = try {
                    documentSnapshot?.toObject<FirestoreUserDto>()
                } catch (e: Exception) {
                    null
                }
                trySend(
                    FirestoreUserEntity(
                        picture = userDto?.picture ?: return@addSnapshotListener,
                        displayName = userDto.displayName ?: return@addSnapshotListener,
                        mailAddress = userDto.mailAddress ?: return@addSnapshotListener,
                        uid = userDto.uid ?: return@addSnapshotListener,
                        currentlyEating = userDto.currentlyEating ?: return@addSnapshotListener,
                        eatingPlaceId = userDto.eatingPlaceId,
                        online = userDto.online ?: false
                    
                    )
                )
            }
        
        awaitClose { registration.remove() }
    }.flowOn(coroutineDispatcherProvider.io)
    
    override suspend fun updateMailAddress(uid: String, newMailAddress: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            try {
                firestore
                    .collection("users")
                    .document(uid)
                    .update("mailAddress", newMailAddress)
                    .await()
                true
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                e.printStackTrace()
                false
            }
        }
    
    override suspend fun updateUserImage(uid: String, pictureUrl: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            try {
                firestore
                    .collection("users")
                    .document(uid)
                    .update("picture", pictureUrl)
                    .await()
                true
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                e.printStackTrace()
                false
            }
        }
    
    override fun getAllFirestoreUsers(): Flow<List<FirestoreUserEntity>> = callbackFlow {
        val registration = firestore.collection("users")
            .addSnapshotListener { documentSnapshot, _ ->
                val firestoreUserEntities = mutableListOf<FirestoreUserEntity>()
                
                documentSnapshot?.documents?.forEach { users ->
                    val userDto = try {
                        users?.toObject<FirestoreUserDto>()
                    } catch (e: Exception) {
                        null
                    }
                    firestoreUserEntities.add(
                        FirestoreUserEntity(
                            picture = userDto?.picture ?: return@addSnapshotListener,
                            displayName = userDto.displayName ?: return@addSnapshotListener,
                            mailAddress = userDto.mailAddress ?: return@addSnapshotListener,
                            uid = userDto.uid ?: return@addSnapshotListener,
                            currentlyEating = userDto.currentlyEating ?: return@addSnapshotListener,
                            eatingPlaceId = userDto.eatingPlaceId,
                            online = userDto.online ?: false
                        )
                    )
                }
                
                trySend(firestoreUserEntities)
            }
        
        awaitClose { registration.remove() }
    }.flowOn(coroutineDispatcherProvider.io)
    
    override suspend fun updateUserPresence(
        uid: String,
        isPresent: Boolean,
    ): Unit = withContext(coroutineDispatcherProvider.io) {
        try {
            firestore
                .collection("users")
                .document(uid)
                .update("online", isPresent)
                .await()
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            e.printStackTrace()
        }
    }
}