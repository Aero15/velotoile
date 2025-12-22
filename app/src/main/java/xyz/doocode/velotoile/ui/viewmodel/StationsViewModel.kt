import android.content.Context
import android.content.SharedPreferences
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
    private var sharedPreferences: SharedPreferences? = null
    
    companion object {
        private const val PREFS_NAME = "velotoile_prefs"
        private const val SORT_FIELD_KEY = "sort_field"
        private const val SORT_ORDER_KEY = "sort_order"
    }
    
    fun initializePreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadPreferences()
    }
    
    private fun loadPreferences() {
        sharedPreferences?.let { prefs ->
            val savedSortFieldName = prefs.getString(SORT_FIELD_KEY, SortField.NUMBER.name)
            val savedSortOrderName = prefs.getString(SORT_ORDER_KEY, SortOrder.ASCENDING.name)
            
            val field = try {
                SortField.valueOf(savedSortFieldName ?: SortField.NUMBER.name)
            } catch (e: IllegalArgumentException) {
                SortField.NUMBER
            }
            
            val order = try {
                SortOrder.valueOf(savedSortOrderName ?: SortOrder.ASCENDING.name)
            } catch (e: IllegalArgumentException) {
                SortOrder.ASCENDING
            }
            
            _sortField.value = field
            _sortOrder.value = order
        }
    }
    
    private fun savePreferences() {
        sharedPreferences?.edit()?.apply {
            putString(SORT_FIELD_KEY, _sortField.value?.name ?: SortField.NUMBER.name)
            putString(SORT_ORDER_KEY, _sortOrder.value?.name ?: SortOrder.ASCENDING.name)
            apply()
        }
    }
    
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
        savePreferences()
        val currentStations = (stations.value as? Resource.Success)?.data ?: emptyList()
        applyFiltersAndSort(currentStations)
    }
    
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        savePreferences()
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
