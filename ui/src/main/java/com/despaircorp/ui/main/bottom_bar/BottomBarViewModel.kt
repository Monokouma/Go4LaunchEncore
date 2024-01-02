package com.despaircorp.ui.main.bottom_bar

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.DisconnectUserUseCase
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantAsFlowUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.firestore.model.CoworkersEntity
import com.despaircorp.domain.location.GetUserLocationEntityAsFlowUseCase
import com.despaircorp.domain.notifications.CreateNotificationChannelUseCase
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.domain.workers.EnqueueLaunchNotificationWorker
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val disconnectUserUseCase: DisconnectUserUseCase,
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase,
    private val enqueueLaunchNotificationWorker: EnqueueLaunchNotificationWorker,
    private val createNotificationChannelUseCase: CreateNotificationChannelUseCase,
    private val getUserLocationEntityAsFlowUseCase: GetUserLocationEntityAsFlowUseCase,
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase,
    private val application: Application,
    private val getCoworkersForSpecificRestaurantAsFlowUseCase: GetCoworkersForSpecificRestaurantAsFlowUseCase
) : ViewModel() {
    private val viewActionMutableLiveData: MutableLiveData<Event<BottomBarAction>> =
        MutableLiveData<Event<BottomBarAction>>()
    val viewAction: LiveData<Event<BottomBarAction>> = viewActionMutableLiveData
    
    init {
        viewModelScope.launch {
            createNotificationChannelUseCase.invoke()
            enqueueLaunchNotificationWorker.invoke()
        }
    }
    
    val viewState: LiveData<BottomBarViewState> = liveData {
        combine(
            getFirestoreUserAsFlowUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid),
            getUserLocationEntityAsFlowUseCase.invoke()
        ) { user, location ->
            
            emit(
                BottomBarViewState(
                    username = user.displayName,
                    emailAddress = user.mailAddress,
                    userImage = user.picture,
                    userLatLn = location.userLatLng,
                    yourLunchSentence = if (user.currentlyEating) {
                        val restaurantEntity = getRestaurantDetailsByPlaceIdUseCase.invoke(
                            user.eatingPlaceId ?: return@combine
                        )
                        val coworkers = getCoworkersForSpecificRestaurantAsFlowUseCase.invoke(
                            user.eatingPlaceId ?: return@combine
                        ).first()
                        
                        StringBuilder()
                            .append(application.getString(R.string.you_eat_at))
                            .append(" ")
                            .append(restaurantEntity?.name)
                            .append(" ")
                            .append(mapCoworkerSentence(coworkers))
                            .toString()
                        
                    } else {
                        application.getString(R.string.no_restaurant_selected)
                    }
                )
            )
        }.collect()
        
    }
    
    private fun mapCoworkerSentence(coworkers: List<CoworkersEntity>): String {
        return if (coworkers.isEmpty()) {
            application.getString(R.string.alone)
        } else {
            StringBuilder()
                .append(application.getString(R.string.with))
                .append(coworkers.joinToString(separator = ", ") { it.name })
                .toString()
        }
    }
    
    fun onDisconnectUser() {
        viewModelScope.launch {
            viewActionMutableLiveData.value = if (disconnectUserUseCase.invoke()) {
                Event(BottomBarAction.OnDisconnect)
            } else {
                Event(BottomBarAction.Error(R.string.error_occurred))
            }
        }
    }
}