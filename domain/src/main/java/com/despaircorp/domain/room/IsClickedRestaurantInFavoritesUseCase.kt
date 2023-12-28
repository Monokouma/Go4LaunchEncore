package com.despaircorp.domain.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class IsClickedRestaurantInFavoritesUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
    
    ) {
    fun invoke(placeId: String): Flow<Boolean> =
        roomDomainRepository.getFavoriteRestaurants().mapLatest { list ->
            var occurrence = false
            list.forEach {
                occurrence = it.placeId == placeId
            }
            occurrence
        }
}