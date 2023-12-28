package com.despaircorp.domain.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class IsClickedRestaurantInFavoritesUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
    
    ) {
    fun invoke(placeId: String): Flow<Boolean> =
        roomDomainRepository.getFavoriteRestaurants().map { list ->
            list.any { it.placeId == placeId }
        }
}