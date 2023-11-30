package com.despaircorp.data.restaurants.dto.search

import com.google.gson.annotations.SerializedName

data class RestaurantsSearchDto(

	@field:SerializedName("candidates")
	val candidates: List<CandidatesItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CandidatesItem(

	@field:SerializedName("place_id")
	val placeId: String? = null
)
