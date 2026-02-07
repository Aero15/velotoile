package xyz.doocode.velotoile.core.util

import SortField
import SortOrder
import android.content.Context
import android.content.SharedPreferences

class Preferences(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "velotoile_prefs"
        private const val SORT_FIELD_KEY = "sort_field"
        private const val SORT_ORDER_KEY = "sort_order"
        private const val FAVORITES_KEY = "favorite_stations"
        private const val LARGE_TILES_KEY = "large_tiles_stations"
        private const val LAST_SCREEN_KEY = "last_screen"
    }

    fun getLastScreen(): String {
        return sharedPreferences.getString(LAST_SCREEN_KEY, "HOME") ?: "HOME"
    }

    fun setLastScreen(screenName: String) {
        sharedPreferences.edit().apply {
            putString(LAST_SCREEN_KEY, screenName)
            apply()
        }
    }

    fun getSortField(): SortField {
        val savedValue = sharedPreferences.getString(SORT_FIELD_KEY, SortField.NUMBER.name)
        return try {
            SortField.valueOf(savedValue ?: SortField.NUMBER.name)
        } catch (e: IllegalArgumentException) {
            SortField.NUMBER
        }
    }

    fun setSortField(field: SortField) {
        sharedPreferences.edit().apply {
            putString(SORT_FIELD_KEY, field.name)
            apply()
        }
    }

    fun getSortOrder(): SortOrder {
        val savedValue = sharedPreferences.getString(SORT_ORDER_KEY, SortOrder.ASCENDING.name)
        return try {
            SortOrder.valueOf(savedValue ?: SortOrder.ASCENDING.name)
        } catch (e: IllegalArgumentException) {
            SortOrder.ASCENDING
        }
    }

    fun setSortOrder(order: SortOrder) {
        sharedPreferences.edit().apply {
            putString(SORT_ORDER_KEY, order.name)
            apply()
        }
    }

    fun saveSortPreferences(field: SortField, order: SortOrder) {
        sharedPreferences.edit().apply {
            putString(SORT_FIELD_KEY, field.name)
            putString(SORT_ORDER_KEY, order.name)
            apply()
        }
    }

    fun getFavoriteStations(): Set<Int> {
        val favoriteString = sharedPreferences.getString(FAVORITES_KEY, "")
        return if (favoriteString.isNullOrEmpty()) {
            emptySet()
        } else {
            favoriteString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    fun isFavorite(stationNumber: Int): Boolean {
        return getFavoriteStations().contains(stationNumber)
    }

    fun addFavorite(stationNumber: Int) {
        val favorites = getFavoriteStations().toMutableSet()
        favorites.add(stationNumber)
        saveFavorites(favorites)
    }

    fun removeFavorite(stationNumber: Int) {
        val favorites = getFavoriteStations().toMutableSet()
        favorites.remove(stationNumber)
        saveFavorites(favorites)
    }

    fun toggleFavorite(stationNumber: Int): Boolean {
        return if (isFavorite(stationNumber)) {
            removeFavorite(stationNumber)
            false
        } else {
            addFavorite(stationNumber)
            true
        }
    }

    private fun saveFavorites(favorites: Set<Int>) {
        val favoriteString = favorites.joinToString(",")
        sharedPreferences.edit().apply {
            putString(FAVORITES_KEY, favoriteString)
            apply()
        }
    }

    // Large Tiles Management
    fun getLargeTileStations(): Set<Int> {
        val largeString = sharedPreferences.getString(LARGE_TILES_KEY, "")
        return if (largeString.isNullOrEmpty()) {
            emptySet()
        } else {
            largeString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    fun isLargeTile(stationNumber: Int): Boolean {
        return getLargeTileStations().contains(stationNumber)
    }

    fun setTileSize(stationNumber: Int, isLarge: Boolean) {
        val largeTiles = getLargeTileStations().toMutableSet()
        if (isLarge) {
            largeTiles.add(stationNumber)
        } else {
            largeTiles.remove(stationNumber)
        }
        saveLargeTiles(largeTiles)
    }

    private fun saveLargeTiles(largeTiles: Set<Int>) {
        sharedPreferences.edit().apply {
            putString(LARGE_TILES_KEY, largeTiles.joinToString(","))
            apply()
        }
    }
}
