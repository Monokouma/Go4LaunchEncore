package com.despaircorp.data.utils

import com.despaircorp.data.firebase_auth.FirebaseAuthDataRepository
import com.despaircorp.data.firebase_real_time.FirebaseRealTimeDataRepository
import com.despaircorp.data.firebase_storage.FirebaseStorageDataRepository
import com.despaircorp.data.firestore.FirestoreDataRepository
import com.despaircorp.data.location.LocationDataRepository
import com.despaircorp.data.notification.NotificationDataRepository
import com.despaircorp.data.restaurants.RestaurantsDataRepository
import com.despaircorp.data.room.RoomDataRepository
import com.despaircorp.data.workers.WorkersDataRepository
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.FirebaseRealTimeDomainRepository
import com.despaircorp.domain.firebase_storage.FirebaseStorageDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.location.LocationDomainRepository
import com.despaircorp.domain.notifications.NotificationDomainRepository
import com.despaircorp.domain.restaurants.RestaurantsDomainRepository
import com.despaircorp.domain.room.RoomDomainRepository
import com.despaircorp.domain.workers.WorkersDomainRepository
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
    
    @Binds
    @Singleton
    abstract fun bindFirebaseStorageRepository(impl: FirebaseStorageDataRepository): FirebaseStorageDomainRepository
    
    @Binds
    @Singleton
    abstract fun bindRoomRepository(impl: RoomDataRepository): RoomDomainRepository
    
    @Binds
    @Singleton
    abstract fun bindRestaurantsRepository(impl: RestaurantsDataRepository): RestaurantsDomainRepository
    
    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationDataRepository): LocationDomainRepository
    
    @Binds
    @Singleton
    abstract fun bindFirebaseRealTimeRepository(impl: FirebaseRealTimeDataRepository): FirebaseRealTimeDomainRepository
    
    @Binds
    @Singleton
    abstract fun bindWorkersRepository(impl: WorkersDataRepository): WorkersDomainRepository
    
    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationDataRepository): NotificationDomainRepository
    
}