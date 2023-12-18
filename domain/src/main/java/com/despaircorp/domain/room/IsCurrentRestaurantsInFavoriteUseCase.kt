package com.despaircorp.domain.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IsCurrentRestaurantsInFavoriteUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository
) {
    fun invoke(placeId: String): Flow<Boolean> = flow {
        if (roomDomainRepository.favoritesTableExist()) {
            roomDomainRepository.getFavoritesRestaurantsEntities().collect { favList ->
                favList.forEach { fav ->
                    
                    emit(fav.placeId == placeId)
                }
            }
        } else {
            emit(false)
        }
        
    }
}