package xyz.doocode.velotoile.ui.viewmodel

import BikeRepository
import Resource
import SortField
import SortOrder
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // New: favorite stations list (only the stations marked as favorite)
    private val _favoriteStations = MutableLiveData<List<Station>>(emptyList())
    val favoriteStations: LiveData<List<Station>> = _favoriteStations

    // Expose raw favorite station numbers for diagnostics and direct consumption
    private val _favoriteNumbers = MutableLiveData<Set<Int>>(emptySet())
    val favoriteNumbers: LiveData<Set<Int>> = _favoriteNumbers

    private val REFRESH_INTERVAL = 120_000L // 120 seconds
    private var preferences: Preferences? = null

    fun initializePreferences(context: Context) {
        preferences = Preferences(context)
        loadPreferences()
        // Initialize favorites view
        updateFavoriteStations()
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
        viewModelScope.launch {
            while (isActive) {
                delay(REFRESH_INTERVAL)
                loadStations()
            }
        }
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

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value = !(_showOnlyFavorites.value ?: false)
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
    }

    /** Toggle favorite status for a station and update cached favorite list. Returns new favorite state. */
    fun toggleFavorite(stationNumber: Int): Boolean {
        val newState = preferences?.toggleFavorite(stationNumber) ?: false
        updateFavoriteStations()
        return newState
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
            else -> filtered
        }

        val finalList = if (sortOrder.value == SortOrder.DESCENDING) sorted.reversed() else sorted
        _favoriteStations.value = finalList
    }
}
