package com.despaircorp.ui.main.coworkers

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.firestore.GetAllCoworkersUseCase
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CoworkersViewModel @Inject constructor(
    private val getAllUserListUseCase: GetAllCoworkersUseCase,
    private val application: Application,
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase
) : ViewModel() {
    
    val viewState = liveData<CoworkersViewState> {
        getAllUserListUseCase.invoke().collect { coworkerEntites ->
            if (latestValue == null) {
                emit(
                    CoworkersViewState(
                        coworkersViewStateItems = emptyList(),
                        isSpinnerVisible = true
                    )
                )
            }
            
            emit(
                CoworkersViewState(
                    coworkersViewStateItems = coworkerEntites.map { coworkersEntity ->
                        CoworkersViewStateItems(
                            picture = coworkersEntity.pictureUrl,
                            eatingMessage = if (coworkersEntity.isEating) {
                                StringBuilder()
                                    .append(coworkersEntity.name)
                                    .append(" ")
                                    .append(
                                        application.getString(
                                            R.string.is_eating,
                                            getRestaurantDetailsByPlaceIdUseCase.invoke(
                                                coworkersEntity.eatingPlaceId ?: return@collect
                                            )?.name
                                        )
                                    )
                                    .toString()
                            } else {
                                StringBuilder()
                                    .append(coworkersEntity.name)
                                    .append(" ")
                                    .append(application.getString(R.string.not_decided))
                                    .toString()
                            },
                            id = coworkersEntity.uid,
                            isEating = coworkersEntity.isEating,
                            textColor = if (coworkersEntity.isEating) {
                                R.color.baby_powder
                            } else {
                                R.color.gray
                            }
                        )
                    }.sortedWith(compareBy({ !it.isEating }, { it.id })),
                    isSpinnerVisible = false
                )
            )
            
        }
    }
}