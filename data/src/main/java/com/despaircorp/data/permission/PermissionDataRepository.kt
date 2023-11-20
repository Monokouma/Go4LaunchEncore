package com.despaircorp.data.permission

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.permission.PermissionDomainRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PermissionDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val application: Application,
    private val appCompatActivity: AppCompatActivity
) : PermissionDomainRepository {
    override suspend fun askForEssentialsPermissions(): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            if (ActivityCompat.checkSelfPermission(
                    application.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    application.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    appCompatActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    0
                )
                true
            } else {
                ensureActive()
                false
            }
        }
}