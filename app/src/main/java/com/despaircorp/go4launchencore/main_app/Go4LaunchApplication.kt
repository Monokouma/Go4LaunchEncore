package com.despaircorp.go4launchencore.main_app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.work.Configuration
import com.despaircorp.domain.firestore.ChangePresenceUseCase
import com.despaircorp.go4launchencore.factory.NotificationWorkerFactory
import com.despaircorp.ui.BuildConfig
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext


@HiltAndroidApp
class Go4LaunchApplication : Application(), Application.ActivityLifecycleCallbacks,
    Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: NotificationWorkerFactory
    
    @Inject
    lateinit var changePresenceUseCase: ChangePresenceUseCase
    
    private val appScope = CoroutineScope(EmptyCoroutineContext)
    private var activeActivity = 0
    
    override fun onCreate() {
        super.onCreate()
        
        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.MAPS_API_KEY);
        }
        
        registerActivityLifecycleCallbacks(this)
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        appScope.cancel()
    }
    
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }
    
    override fun onActivityStarted(activity: Activity) {
        activeActivity++
        appScope.launch {
            changePresenceUseCase.invoke(activeActivity > 0)
        }
    }
    
    override fun onActivityResumed(activity: Activity) {
    
    }
    
    override fun onActivityPaused(activity: Activity) {
    }
    
    override fun onActivityStopped(activity: Activity) {
        activeActivity--
        appScope.launch {
            changePresenceUseCase.invoke(activeActivity > 0)
        }
    }
    
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }
    
    override fun onActivityDestroyed(activity: Activity) {
    }
    
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setWorkerFactory(workerFactory).build()
}