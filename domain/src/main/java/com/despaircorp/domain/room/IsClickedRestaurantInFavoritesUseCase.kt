package com.despaircorp.domain.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsClickedRestaurantInFavoritesUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
    
    ) {
    fun invoke(placeId: String): Flow<Boolean> = flow {
        roomDomainRepository.getFavoriteRestaurants().map { list ->
            list.any { it.placeId == placeId }
        }.collect(this)
    }
    
}