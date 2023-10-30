package com.despaircorp.data.restaurants.dto.details

import com.despaircorp.data.restaurants.dto.list.Geometry
import com.despaircorp.data.restaurants.dto.list.Location
import com.despaircorp.data.restaurants.dto.list.Northeast
import com.despaircorp.data.restaurants.dto.list.PhotosItem
import com.despaircorp.data.restaurants.dto.list.PlusCode
import com.despaircorp.data.restaurants.dto.list.Southwest
import com.despaircorp.data.restaurants.dto.list.Viewport
import com.google.gson.annotations.SerializedName

data class RestaurantDetailsDto(
    
    @field:SerializedName("result")
    val result: Result? = null,
    
    @field:SerializedName("html_attributions")
    val htmlAttributions: List<Any?>? = null,
    
    @field:SerializedName("status")
    val status: String? = null
)

data class Viewport(
    
    @field:SerializedName("southwest")
    val southwest: Southwest? = null,
    
    @field:SerializedName("northeast")
    val northeast: Northeast? = null
)

data class PlusCode(
    
    @field:SerializedName("compound_code")
    val compoundCode: String? = null,
    
    @field:SerializedName("global_code")
    val globalCode: String? = null
)

data class EditorialSummary(
    
    @field:SerializedName("overview")
    val overview: String? = null,
    
    @field:SerializedName("language")
    val language: String? = null
)

data class AddressComponentsItem(
    
    @field:SerializedName("types")
    val types: List<String?>? = null,
    
    @field:SerializedName("short_name")
    val shortName: String? = null,
    
    @field:SerializedName("long_name")
    val longName: String? = null
)

data class Northeast(
    
    @field:SerializedName("lng")
    val lng: Any? = null,
    
    @field:SerializedName("lat")
    val lat: Any? = null
)

data class Southwest(
    
    @field:SerializedName("lng")
    val lng: Any? = null,
    
    @field:SerializedName("lat")
    val lat: Any? = null
)

data class PhotosItem(
    
    @field:SerializedName("photo_reference")
    val photoReference: String? = null,
    
    @field:SerializedName("width")
    val width: Int? = null,
    
    @field:SerializedName("html_attributions")
    val htmlAttributions: List<String?>? = null,
    
    @field:SerializedName("height")
    val height: Int? = null
)

data class Location(
    
    @field:SerializedName("lng")
    val lng: Double? = null,
    
    @field:SerializedName("lat")
    val lat: Double? = null
)

data class Result(
    
    @field:SerializedName("utc_offset")
    val utcOffset: Int? = null,
    
    @field:SerializedName("formatted_address")
    val formattedAddress: String? = null,
    
    @field:SerializedName("wheelchair_accessible_entrance")
    val wheelchairAccessibleEntrance: Boolean? = null,
    
    @field:SerializedName("icon")
    val icon: String? = null,
    
    @field:SerializedName("rating")
    val rating: Any? = null,
    
    @field:SerializedName("icon_background_color")
    val iconBackgroundColor: String? = null,
    
    @field:SerializedName("photos")
    val photos: List<PhotosItem?>? = null,
    
    @field:SerializedName("editorial_summary")
    val editorialSummary: EditorialSummary? = null,
    
    @field:SerializedName("reference")
    val reference: String? = null,
    
    @field:SerializedName("user_ratings_total")
    val userRatingsTotal: Int? = null,
    
    @field:SerializedName("reviews")
    val reviews: List<ReviewsItem?>? = null,
    
    @field:SerializedName("icon_mask_base_uri")
    val iconMaskBaseUri: String? = null,
    
    @field:SerializedName("adr_address")
    val adrAddress: String? = null,
    
    @field:SerializedName("place_id")
    val placeId: String? = null,
    
    @field:SerializedName("types")
    val types: List<String?>? = null,
    
    @field:SerializedName("website")
    val website: String? = null,
    
    @field:SerializedName("business_status")
    val businessStatus: String? = null,
    
    @field:SerializedName("address_components")
    val addressComponents: List<AddressComponentsItem?>? = null,
    
    @field:SerializedName("url")
    val url: String? = null,
    
    @field:SerializedName("name")
    val name: String? = null,
    
    @field:SerializedName("geometry")
    val geometry: Geometry? = null,
    
    @field:SerializedName("vicinity")
    val vicinity: String? = null,
    
    @field:SerializedName("plus_code")
    val plusCode: PlusCode? = null,
    
    @field:SerializedName("formatted_phone_number")
    val formattedPhoneNumber: String? = null,
    
    @field:SerializedName("international_phone_number")
    val internationalPhoneNumber: String? = null
)

data class Geometry(
    
    @field:SerializedName("viewport")
    val viewport: Viewport? = null,
    
    @field:SerializedName("location")
    val location: Location? = null
)

data class ReviewsItem(
    
    @field:SerializedName("author_name")
    val authorName: String? = null,
    
    @field:SerializedName("profile_photo_url")
    val profilePhotoUrl: String? = null,
    
    @field:SerializedName("original_language")
    val originalLanguage: String? = null,
    
    @field:SerializedName("author_url")
    val authorUrl: String? = null,
    
    @field:SerializedName("rating")
    val rating: Int? = null,
    
    @field:SerializedName("language")
    val language: String? = null,
    
    @field:SerializedName("text")
    val text: String? = null,
    
    @field:SerializedName("time")
    val time: Int? = null,
    
    @field:SerializedName("translated")
    val translated: Boolean? = null,
    
    @field:SerializedName("relative_time_description")
    val relativeTimeDescription: String? = null
)
