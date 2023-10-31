package com.despaircorp.data.firebase_auth

import com.despaircorp.data.firebase_auth.dto.AuthenticateUserDto
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseAuthDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val firebaseAuth: FirebaseAuth,
) : FirebaseAuthDomainRepository {
    override suspend fun isUserAlreadyAuth(): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                if (firebaseAuth.currentUser == null) {
                    cont.resume(false)
                } else {
                    cont.resume(true)
                }
            }
        }
    
    override suspend fun isUserWithCredentialsExist(mail: String, password: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                firebaseAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener {
                        if (it.exception == null) {
                            cont.resume(true)
                        } else {
                            cont.resume(false)
                        }
                    }
            }
        }
    
    override suspend fun createUserWithCredentials(
        mail: String,
        password: String
    ): Boolean = withContext(coroutineDispatcherProvider.io) {
        suspendCancellableCoroutine { cont ->
            val task = firebaseAuth.createUserWithEmailAndPassword(mail, password)
            
            task.addOnCompleteListener {
                if (it.exception == null) {
                    if (it.isSuccessful) {
                        cont.resume(true)
                    } else {
                        cont.resume(false)
                    }
                } else {
                    cont.resume(false)
                }
            }
        }
    }
    
    override suspend fun getCurrentAuthenticatedUser(): AuthenticateUserEntity =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                val dto = AuthenticateUserDto(
                    displayName = firebaseAuth.currentUser?.displayName,
                    emailAddress = firebaseAuth.currentUser?.email,
                    picture = firebaseAuth.currentUser?.photoUrl,
                    uid = firebaseAuth.currentUser?.uid
                )
                
                cont.resume(
                    AuthenticateUserEntity(
                        picture = dto.picture?.path
                            ?: "https://w0.peakpx.com/wallpaper/733/998/HD-wallpaper-hedgedog-on-cloth-in-blur-green-bokeh-background-animals.jpg",
                        displayName = dto.displayName ?: "none",
                        mailAddress = dto.emailAddress ?: return@suspendCancellableCoroutine,
                        uid = dto.uid ?: return@suspendCancellableCoroutine,
                        currentlyEating = false,
                        eatingPlaceId = null
                    )
                )
            }
        }
    
    override suspend fun signInTokenUser(token: AccessToken): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                
                firebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(token.token))
                    .addOnCompleteListener { task ->
                        if (task.exception == null) {
                            cont.resume(task.isSuccessful)
                        } else {
                            cont.resume(false)
                        }
                    }
            }
        }
    
    override suspend fun disconnectUser(): Boolean = withContext(coroutineDispatcherProvider.io) {
        try {
            firebaseAuth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun updateMailAddress(newMailAddress: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                if (firebaseAuth.currentUser == null) {
                    cont.resume(false)
                } else {
                    firebaseAuth.currentUser?.updateEmail(newMailAddress)
                        ?.addOnCompleteListener { task ->
                            if (task.exception == null) {
                                cont.resume(task.isSuccessful)
                            } else {
                                cont.resume(false)
                            }
                        }
                }
            }
        }
    
    override suspend fun updatePassword(newPassword: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                if (firebaseAuth.currentUser == null) {
                    cont.resume(false)
                } else {
                    firebaseAuth.currentUser?.updatePassword(newPassword)
                        ?.addOnCompleteListener { task ->
                            if (task.exception == null) {
                                cont.resume(task.isSuccessful)
                            } else {
                                cont.resume(false)
                            }
                        }
                }
            }
        }
    
    override fun getCurrentFacebookAccessToken(): String? =
        AccessToken.getCurrentAccessToken()?.token
}