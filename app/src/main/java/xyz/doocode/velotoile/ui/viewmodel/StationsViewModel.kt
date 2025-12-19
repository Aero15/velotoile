import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.doocode.velotoile.core.dto.Station

class StationsViewModel : ViewModel() {
    private val repository = BikeRepository()
    
    private val _stations = MutableLiveData<Resource<List<Station>>>()
    val stations: LiveData<Resource<List<Station>>> = _stations
    
    fun loadStations() {
        viewModelScope.launch {
            _stations.value = Resource.Loading()
            _stations.value = repository.getStations()
        }
    }
}