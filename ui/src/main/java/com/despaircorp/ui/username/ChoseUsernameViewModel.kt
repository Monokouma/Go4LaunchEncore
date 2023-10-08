package com.despaircorp.ui.username

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.UpdateUsernameUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChoseUsernameViewModel @Inject constructor(
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase,
) : ViewModel() {
    private var username: String? = null
    
    val viewAction = MutableLiveData<Event<ChoseUsernameAction>>()
    fun onUsernameTextChanged(usernameInput: String) {
        username = usernameInput
    }
    
    fun onContinueButtonClicked() {
        if (username.isNullOrEmpty()) {
            viewAction.value = Event(ChoseUsernameAction.Error(R.string.error_username_empty))
        } else {
            viewModelScope.launch {
                if (updateUsernameUseCase.invoke(
                        username ?: return@launch,
                        getAuthenticatedUserUseCase.invoke().uid
                    )
                ) {
                    viewAction.value = Event(ChoseUsernameAction.Continue)
                } else {
                    viewAction.value = Event(ChoseUsernameAction.Error(R.string.error_occurred))
                }
            }
        }
    }
    
}