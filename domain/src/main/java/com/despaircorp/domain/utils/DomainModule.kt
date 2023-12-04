package com.despaircorp.domain.utils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Singleton
    @Provides
    fun provideClock(): Clock = Clock.systemDefaultZone()
}