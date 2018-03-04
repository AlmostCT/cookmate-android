package almostct.top.foodhack

import almostct.top.foodhack.api.Client
import android.app.Application
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.yandex.speechkit.SpeechKit

class App: Application() {

    lateinit var client: Client
    private val API_KEY_FOR_TESTS_ONLY = "b262e652-ed35-4a4d-9ef2-7e865c5a35f3"

    override fun onCreate() {
        super.onCreate()
        SpeechKit.getInstance().configure(applicationContext, API_KEY_FOR_TESTS_ONLY);
        initialize()
    }

    private fun initialize() {
        client = Retrofit.Builder().baseUrl("http://192.168.43.225:8080/")
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
