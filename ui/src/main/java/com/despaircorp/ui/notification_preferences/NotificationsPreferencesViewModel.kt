package com.despaircorp.ui.notification_preferences

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.room.UpdateNotificationStateUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsPreferencesViewModel @Inject constructor(
    private val updateNotificationStateUseCase: UpdateNotificationStateUseCase,
) : ViewModel() {
    
    val viewAction = MutableLiveData<Event<NotificationsPreferencesAction>>()
    
    fun onNotificationStateChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            viewAction.value = if (updateNotificationStateUseCase.invoke(isEnabled)) {
                Event(NotificationsPreferencesAction.Success)
            } else {
                Event(NotificationsPreferencesAction.Error(message = R.string.error_occurred))
            }
        }
    }
}