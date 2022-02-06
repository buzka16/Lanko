package android.example.tinkoff.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


private const val BASE_URL = "https://developerslife.ru/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("random?json=true")
    suspend fun getRandom(): Response<String?>

    @GET
    suspend fun getLatest(@Url url: String): Response<String?>

    @GET
    suspend fun getBest(@Url url: String): Response<String?>
}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}