import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import xyz.doocode.velotoile.core.dto.Station

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
    
    private val _filteredStations = MutableLiveData<List<Station>>()
    val filteredStations: LiveData<List<Station>> = _filteredStations
    
    private val REFRESH_INTERVAL = 120_000L // 60 seconds
    
    fun loadStations() {
        viewModelScope.launch {
            _stations.value = Resource.Loading()
            val result = repository.getStations()
            _stations.value = result
            if (result is Resource.Success) {
                applyFiltersAndSort(result.data ?: emptyList())
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
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
    }
    
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
    }
    
    private fun applyFiltersAndSort(stationsList: List<Station>) {
        val query = searchQuery.value?.lowercase() ?: ""
        var filtered = stationsList
        
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
}
