package com.despaircorp.domain.firebase_real_time

import javax.inject.Inject

class GetOrderedUidsUseCase @Inject constructor() {
    fun invoke(uid1: String, uid2: String): Pair<String, String> = if (uid1 > uid2) {
        Pair(uid2, uid1)
    } else {
        Pair(uid1, uid2)
    }
}