package com.despaircorp.ui.main.coworkers

import androidx.annotation.ColorRes

data class CoworkersViewStateItems(
    val picture: String,
    val eatingMessage: String,
    val id: String,
    val isEating: Boolean,
    @ColorRes val textColor: Int
)