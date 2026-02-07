import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.doocode.velotoile.BuildConfig
import xyz.doocode.velotoile.core.api.DateDeserializer
import xyz.doocode.velotoile.core.api.JCDecauxApiService
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://api.jcdecaux.com/"
    const val API_KEY = BuildConfig.ApiKey_JCDecaux
    const val CONTRACT_NAME = "besancon"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(Long::class.java, DateDeserializer)
        .create()

    val jcDecauxApi: JCDecauxApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(JCDecauxApiService::class.java)
    }
}