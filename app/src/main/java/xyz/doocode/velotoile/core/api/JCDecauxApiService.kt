package xyz.doocode.velotoile.core.api

import retrofit2.http.*
import xyz.doocode.velotoile.core.dto.Station

interface JCDecauxApiService {
    @GET("vls/v1/stations")
    suspend fun getStations(
        @Query("contract") contract: String,
        @Query("apiKey") apiKey: String
    ): List<Station>
    
    @GET("vls/v1/stations/{station_number}")
    suspend fun getStation(
        @Path("station_number") stationNumber: Int,
        @Query("contract") contract: String,
        @Query("apiKey") apiKey: String
    ): Station
}