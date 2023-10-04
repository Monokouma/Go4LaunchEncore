package com.despaircorp.data.utils

import com.despaircorp.data.firebaseAuth.FirebaseAuthDataRepository
import com.despaircorp.data.firestore.FirestoreDataRepository
import com.despaircorp.domain.firebaseAuth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
abstract class DataBindModule {
    @Binds
    @Singleton
    abstract fun bindFirebaseAuthRepository(impl: FirebaseAuthDataRepository): FirebaseAuthDomainRepository
    
    @Binds
    @Singleton
    abstract fun bindFirestoreRepository(impl: FirestoreDataRepository): FirestoreDomainRepository
}