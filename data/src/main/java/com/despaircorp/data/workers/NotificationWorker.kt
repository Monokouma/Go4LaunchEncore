package com.despaircorp.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.despaircorp.data.R
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantAsFlowUseCase
import com.despaircorp.domain.firestore.model.CoworkersEntity
import com.despaircorp.domain.notifications.NotificationDomainRepository
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val notificationDomainRepository: NotificationDomainRepository,
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase,
    private val getCoworkersForSpecificRestaurantAsFlowUseCase: GetCoworkersForSpecificRestaurantAsFlowUseCase
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val user =
            firestoreDomainRepository.getUser(firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid)
        
        val sentence = if (user.currentlyEating) {
            val restaurantEntity = getRestaurantDetailsByPlaceIdUseCase.invoke(
                user.eatingPlaceId ?: return Result.failure()
            )
            val coworkers = getCoworkersForSpecificRestaurantAsFlowUseCase.invoke(
                user.eatingPlaceId ?: return Result.failure()
            ).first()
            
            StringBuilder()
                .append(applicationContext.getString(R.string.you_eat_at))
                .append(" ")
                .append(restaurantEntity?.name)
                .append(" ")
                .append(mapCoworkerSentence(coworkers))
                .toString()
            
        } else {
            applicationContext.getString(R.string.no_restaurant_selected)
        }
        
        notificationDomainRepository.notify(user.displayName, sentence)
        
        return Result.success()
    }
    
    private fun mapCoworkerSentence(coworkers: List<CoworkersEntity>): String {
        return if (coworkers.isEmpty()) {
            applicationContext.getString(R.string.alone)
        } else {
            StringBuilder()
                .append(applicationContext.getString(R.string.with))
                .append(coworkers.joinToString(separator = ", ") { it.name })
                .toString()
        }
    }
}