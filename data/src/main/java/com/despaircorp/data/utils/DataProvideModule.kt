package com.despaircorp.data.utils

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.despaircorp.data.retrofit.GooglePlacesApi
import com.despaircorp.data.room.dao.FavoritesRestaurantsDao
import com.despaircorp.data.room.dao.UserPreferencesDao
import com.despaircorp.data.room.database.Go4LunchRoomDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataProvideModule {
    
    @Provides
    @Singleton
    fun provideResources(application: Application): Resources {
        return application.resources
    }
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application)
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideGooglePlaceApi(retrofit: Retrofit): GooglePlacesApi {
        return retrofit.create(GooglePlacesApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideRoomDataBase(application: Application): Go4LunchRoomDatabase =
        Go4LunchRoomDatabase.getDatabase(application.applicationContext)
    
    @Provides
    @Singleton
    fun provideUserPreferencesDao(application: Application): UserPreferencesDao =
        Go4LunchRoomDatabase.getDatabase(application.applicationContext).userPreferencesDao()
    
    @Provides
    @Singleton
    fun provideFavoritesRestaurantDao(application: Application): FavoritesRestaurantsDao =
        Go4LunchRoomDatabase.getDatabase(application.applicationContext).favoritesRestaurantsDao()
    
    @Provides
    @Singleton
    fun provideFirebaseStorageReference(): FirebaseStorage =
        FirebaseStorage.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideWorkManager(application: Application): WorkManager {
        return WorkManager.getInstance(application)
    }
    
    @Provides
    @Singleton
    fun provideAppCompatActivityManager(application: Application): AppCompatActivity {
        return AppCompatActivity()
    }
    
    @Provides
    @Singleton
    fun provideNotificationCompatManager(application: Application): NotificationManagerCompat {
        return NotificationManagerCompat.from(application)
    }
}