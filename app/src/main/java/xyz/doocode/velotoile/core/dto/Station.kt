package xyz.doocode.velotoile.core.dto

import com.google.gson.annotations.SerializedName

data class Station (
    val number: Int,
    val name: String,
    val address: String,
    val position: Position,
    val banking: Boolean,
    val bonus: Boolean,
    val status: String,
    val connected: Boolean,
    @SerializedName("contract_name")
    val contractName: String,
    @SerializedName("bike_stands")
    val bikeStands: Int,
    @SerializedName("available_bike_stands")
    val availableBikeStands: Int,
    @SerializedName("available_bikes")
    val availableBikes: Int,
    @SerializedName("last_update")
    val lastUpdate: Long,
    @SerializedName("total_stands")
    val totalStands: Stand,
    @SerializedName("main_stands")
    val mainStands: Stand
)

data class Position (
    val latitude: Double,
    val longitude: Double
)

data class Stand (
    val availabilities: Availabilities,
    val capacity: Int
)

data class Availabilities (
    val bikes: Int,
    val stands: Int,
    @SerializedName("mechanical_bikes")
    val mechanicalBikes: Int,
    @SerializedName("electrical_bikes")
    val electricalBikes: Int,
    @SerializedName("electrical_internal_batteries")
    val electricalInternalBatteries: Int,
    @SerializedName("electrical_removable_batteries")
    val electricalRemovableBatteries: Int
)