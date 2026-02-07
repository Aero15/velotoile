package xyz.doocode.velotoile.ui.viewmodel

import BikeRepository
import Resource
import SortField
import SortOrder
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import xyz.doocode.velotoile.core.dto.Station
import xyz.doocode.velotoile.core.util.Preferences

class StationsViewModel : ViewModel() {
    private val repository = BikeRepository()

    private val _stations = MutableLiveData<Resource<List<Station>>>()
    val stations: LiveData<Resource<List<Station>>> = _stations

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _sortField = MutableLiveData<SortField>(SortField.NUMBER)
    val sortField: LiveData<SortField> = _sortField

    private val _sortOrder = MutableLiveData<SortOrder>(SortOrder.ASCENDING)
    val sortOrder: LiveData<SortOrder> = _sortOrder

    private val _showOnlyFavorites = MutableLiveData<Boolean>(false)
    val showOnlyFavorites: LiveData<Boolean> = _showOnlyFavorites

    private val _filteredStations = MutableLiveData<List<Station>>()
    val filteredStations: LiveData<List<Station>> = _filteredStations

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> = _userLocation

    // New: favorite stations list (only the stations marked as favorite)
    private val _favoriteStations = MutableLiveData<List<Station>>(emptyList())
    val favoriteStations: LiveData<List<Station>> = _favoriteStations

    // Expose raw favorite station numbers for diagnostics and direct consumption
    private val _favoriteNumbers = MutableLiveData<Set<Int>>(emptySet())
    val favoriteNumbers: LiveData<Set<Int>> = _favoriteNumbers

    // Large tiles support
    private val _largeTileStations = MutableLiveData<Set<Int>>(emptySet())
    val largeTileStations: LiveData<Set<Int>> = _largeTileStations

    private val REFRESH_INTERVAL = 120_000L // 120 seconds
    private var preferences: Preferences? = null
    private var refreshJob: Job? = null

    fun initializePreferences(context: Context) {
        preferences = Preferences(context)
        loadPreferences()
        // Initialize favorites view
        updateFavoriteStations()
        loadLargeTiles()
    }

    private fun loadLargeTiles() {
        _largeTileStations.value = preferences?.getLargeTileStations() ?: emptySet()
    }

    fun toggleStationSize(stationNumber: Int) {
        val current = preferences?.isLargeTile(stationNumber) ?: false
        preferences?.setTileSize(stationNumber, !current)
        loadLargeTiles()
    }

    fun getLastScreen(): String {
        return preferences?.getLastScreen() ?: "HOME"
    }

    fun setLastScreen(screenName: String) {
        preferences?.setLastScreen(screenName)
    }

    private fun loadPreferences() {
        preferences?.let { prefManager ->
            val field = prefManager.getSortField()
            val order = prefManager.getSortOrder()

            _sortField.value = field
            _sortOrder.value = order
        }
    }

    fun loadStations() {
        viewModelScope.launch {
            _stations.value = Resource.Loading()
            val result = repository.getStations()
            _stations.value = result
            if (result is Resource.Success) {
                applyFiltersAndSort(result.data ?: emptyList())
                updateFavoriteStations(result.data ?: emptyList())
            }
        }
    }

    fun startAutoRefresh() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            while (isActive) {
                delay(REFRESH_INTERVAL)
                loadStations()
            }
        }
    }

    fun stopAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
    }

    fun setSortField(field: SortField) {
        _sortField.value = field
        preferences?.setSortField(field)
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
        updateFavoriteStations()
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        preferences?.setSortOrder(order)
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
        updateFavoriteStations()
    }

    /** Toggle favorite status for a station and update cached favorite list. Returns new favorite state. */
    fun toggleFavorite(stationNumber: Int): Boolean {
        val newState = preferences?.toggleFavorite(stationNumber) ?: false
        updateFavoriteStations()
        return newState
    }

    fun updateUserLocation(location: Location) {
        _userLocation.value = location
        // re-trigger sort
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
    }

    private fun applyFiltersAndSort(stationsList: List<Station>) {
        val query = searchQuery.value?.trim()?.lowercase() ?: ""
        var filtered = stationsList

        // Apply favorites filter
        if (_showOnlyFavorites.value == true) {
            val favoriteNumbers = preferences?.getFavoriteStations() ?: emptySet()
            filtered = filtered.filter { station ->
                station.number in favoriteNumbers
            }
        }

        // Apply search filter
        if (query.isNotEmpty()) {
            filtered = filtered.filter { station ->
                station.name.lowercase().contains(query) ||
                        station.address.lowercase().contains(query)
            }
        }

        // Apply sorting
        val sorted = when (sortField.value) {
            SortField.NAME -> filtered.sortedBy { it.name }
            SortField.NUMBER -> filtered.sortedBy { it.number }
            SortField.TOTAL_BIKES -> filtered.sortedBy { it.totalStands.availabilities.bikes }
            SortField.MECHANICAL_BIKES -> filtered.sortedBy { it.totalStands.availabilities.mechanicalBikes }
            SortField.ELECTRICAL_BIKES -> filtered.sortedBy { it.totalStands.availabilities.electricalBikes }
            SortField.AVAILABLE_STANDS -> filtered.sortedBy { it.totalStands.availabilities.stands }
            SortField.PROXIMITY -> {
                val loc = _userLocation.value
                if (loc != null) {
                    filtered.sortedBy {
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            loc.latitude,
                            loc.longitude,
                            it.position.latitude,
                            it.position.longitude,
                            results
                        )
                        results[0]
                    }
                } else filtered
            }

            else -> filtered
        }

        val finalList = if (sortOrder.value == SortOrder.DESCENDING) sorted.reversed() else sorted
        _filteredStations.value = finalList
    }

    private fun updateFavoriteStations(stationsList: List<Station>? = null) {
        val all = stationsList ?: (stations.value as? Resource.Success)?.data ?: emptyList()
        val favoriteNumbers = preferences?.getFavoriteStations() ?: emptySet()
        _favoriteNumbers.value = favoriteNumbers
        var filtered = all.filter { it.number in favoriteNumbers }

        // Apply current sort field and order to favorites as well
        val sorted = when (sortField.value) {
            SortField.NAME -> filtered.sortedBy { it.name }
            SortField.NUMBER -> filtered.sortedBy { it.number }
            SortField.TOTAL_BIKES -> filtered.sortedBy { it.totalStands.availabilities.bikes }
            SortField.MECHANICAL_BIKES -> filtered.sortedBy { it.totalStands.availabilities.mechanicalBikes }
            SortField.ELECTRICAL_BIKES -> filtered.sortedBy { it.totalStands.availabilities.electricalBikes }
            SortField.AVAILABLE_STANDS -> filtered.sortedBy { it.totalStands.availabilities.stands }
            SortField.PROXIMITY -> {
                val loc = _userLocation.value
                if (loc != null) {
                    filtered.sortedBy {
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            loc.latitude,
                            loc.longitude,
                            it.position.latitude,
                            it.position.longitude,
                            results
                        )
                        results[0]
                    }
                } else filtered
            }

            else -> filtered
        }

        val finalList = if (sortOrder.value == SortOrder.DESCENDING) sorted.reversed() else sorted
        _favoriteStations.value = finalList
    }
}
