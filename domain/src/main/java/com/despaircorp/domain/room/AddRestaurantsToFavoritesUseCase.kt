package com.despaircorp.domain.room

import com.despaircorp.domain.room.model.FavoritesRestaurantsDomainEntity
import javax.inject.Inject

class AddRestaurantsToFavoritesUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository
) {
    suspend fun invoke(placeId: String): Boolean = roomDomainRepository.insertFavoriteRestaurant(
        FavoritesRestaurantsDomainEntity(
            placeId
        )
    ) >= 1L
}