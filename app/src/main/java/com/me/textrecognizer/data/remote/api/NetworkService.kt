package com.me.textrecognizer.data.remote.api

import com.me.textrecognizer.data.remote.dto.DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("maps/api/distancematrix/json")
    suspend fun getDirections(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("mode") mode: String,
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}