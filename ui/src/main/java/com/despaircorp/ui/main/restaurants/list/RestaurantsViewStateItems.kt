package com.despaircorp.ui.main.restaurants.list

import androidx.annotation.ColorRes
import com.despaircorp.ui.utils.NativeText

data class RestaurantsViewStateItems(
    val restaurantName: String,
    val restaurantDistance: String,
    val restaurantImageUrl: String?,
    val restaurantAddressAndType: String,
    val workmatesInside: String?,
    val restaurantSchedule: NativeText?,
    val restaurantStar: Double?,
    @ColorRes val openedTextColorRes: Int?,
    val placeId: String
)