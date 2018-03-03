package almostct.top.foodhack

import almostct.top.foodhack.api.Client
import android.app.Application
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

class App: Application() {

    lateinit var client: Client

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        client = Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(
                JacksonConverterFactory.create(
                    jacksonObjectMapper().configure(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false
                    )
                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(Client::class.java)
    }
}
