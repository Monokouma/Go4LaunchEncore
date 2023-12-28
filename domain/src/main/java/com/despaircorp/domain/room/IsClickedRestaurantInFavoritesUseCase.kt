package com.despaircorp.domain.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class IsClickedRestaurantInFavoritesUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
    
    ) {
    fun invoke(placeId: String): Flow<Boolean> =
        roomDomainRepository.getFavoriteRestaurants().transform { list ->
            list.forEach {
                if (it.placeId == placeId) {
                    emit(true)
                }
            }
            emit(false)
        }
}