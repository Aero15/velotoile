package xyz.doocode.velotoile.core.util

import android.content.Context
import android.content.Intent
import xyz.doocode.velotoile.core.dto.Station
import androidx.core.net.toUri

fun openMapsIntent(
    context: Context,
    station: Station
) {
    val latitude = station.position.latitude
    val longitude = station.position.longitude
    val stationName = station.name

    // Format: geo:latitude,longitude?q=latitude,longitude(name)
    val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude($stationName)"

    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = geoUri.toUri()
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Si aucune application de carte n'est disponible
        try {
            // Fallback vers Google Maps
            val mapsUri = "https://maps.google.com/maps?q=$latitude,$longitude"
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                mapsUri.toUri()
            )
            context.startActivity(webIntent)
        } catch (e: Exception) {
            // Silencieusement ignorer l'erreur
        }
    }
}
