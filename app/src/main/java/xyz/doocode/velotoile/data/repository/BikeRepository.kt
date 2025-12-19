import retrofit2.HttpException
import xyz.doocode.velotoile.core.dto.Station
import java.io.IOException

class BikeRepository {
    private val api = ApiClient.jcDecauxApi
    
    suspend fun getStations(): Resource<List<Station>> {
        return try {
            val response = api.getStations(
                contract = ApiClient.CONTRACT_NAME,
                apiKey = ApiClient.API_KEY
            )
            Resource.Success(response)
        } catch (e: HttpException) {
            Resource.Error("Erreur réseau: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Vérifiez votre connexion internet")
        } catch (e: Exception) {
            Resource.Error("Erreur inattendue: ${e.localizedMessage}")
        }
    }
    
    suspend fun getStation(stationNumber: Int): Resource<Station> {
        return try {
            val response = api.getStation(
                stationNumber = stationNumber,
                contract = ApiClient.CONTRACT_NAME,
                apiKey = ApiClient.API_KEY
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Erreur: ${e.localizedMessage}")
        }
    }
}