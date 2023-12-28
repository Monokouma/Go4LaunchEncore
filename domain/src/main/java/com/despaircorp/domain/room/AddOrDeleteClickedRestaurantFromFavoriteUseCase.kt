package com.despaircorp.domain.room

import com.despaircorp.domain.room.model.ClickedRestaurantsEntity
import javax.inject.Inject

class AddOrDeleteClickedRestaurantFromFavoriteUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository
) {
    suspend fun invoke(hadToAddClickedRestaurant: Boolean, placeId: String): Boolean {
        if (hadToAddClickedRestaurant) {
            return (roomDomainRepository.insertNewClickedRestaurantToFavorite(
                ClickedRestaurantsEntity(placeId)
            ) >= 1L)
        } else {
            return (roomDomainRepository.removeClickedRestaurantToFavorite(
                ClickedRestaurantsEntity(
                    placeId
                )
            ) >= 1)
            
        }
    }
}