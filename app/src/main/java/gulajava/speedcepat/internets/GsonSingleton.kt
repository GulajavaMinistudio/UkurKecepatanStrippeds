package gulajava.speedcepat.internets

import com.google.gson.Gson

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
object GsonSingleton {

    private lateinit var mGson: Gson

    fun getGson(): Gson {
        if (!this::mGson.isInitialized) {
            mGson = Gson()
        }
        return mGson
    }
}