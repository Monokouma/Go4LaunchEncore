package com.despaircorp.ui.utils

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat

fun TextView.setTextColorRes(@ColorRes colorRes: Int?) {
    if (colorRes != null) {
        setTextColor(
            ResourcesCompat.getColor(
                resources,
                colorRes,
                null
            )
        )
    }
}