package com.despaircorp.data.retrofit

import com.despaircorp.data.restaurants.dto.details.RestaurantDetailsDto
import com.despaircorp.data.restaurants.dto.list.RestaurantsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String,
        @Query("type") type: String
    ): RestaurantsDto
    
    @GET("/maps/api/place/details/json")
    suspend fun getPlacesDetails(
        @Query("key") apiKey: String,
        @Query("place_id") placeId: String
    ): RestaurantDetailsDto?
    
    /*
    @GET("maps/api/place/findplacefromtext/json")
    suspend fun getPlacesSearchResult(
        @Query("input") input: String,
        @Query("inputtype") inputType: String,
        @Query("fields") fields: String,
        @Query("key") apiKey: String
    ): RestaurantsSearchDto?
    
     */
}