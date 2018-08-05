package gulajava.speedcepat.internets

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
object OkHttpSingleton {

    private lateinit var mOkHttpClient: OkHttpClient

    init {
        if (!this::mOkHttpClient.isInitialized) {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)
            builder.writeTimeout(30, TimeUnit.SECONDS)
            builder.addInterceptor(getInterceptor())

            mOkHttpClient = builder.build()
        }
    }

    private fun getInterceptor(): Interceptor {

        return Interceptor { chain: Interceptor.Chain ->
            val newRequest = chain.request().newBuilder()
                //.addHeader("Accept", "application/json")
                .build()
            return@Interceptor chain.proceed(newRequest)
        }
    }

    fun getOkHttpClient(): OkHttpClient {
        return mOkHttpClient
    }

}