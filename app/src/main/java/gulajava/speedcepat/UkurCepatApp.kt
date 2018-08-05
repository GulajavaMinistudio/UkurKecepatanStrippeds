package gulajava.speedcepat

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import gulajava.speedcepat.database.models.MyObjectBox
import gulajava.speedcepat.internets.OkHttpSingleton
import io.objectbox.BoxStore

/**
 * Created by Gulajava Ministudio on 5/2/18.
 */
class UkurCepatApp : Application() {

    companion object {

        lateinit var instances: UkurCepatApp
            private set
    }

    private lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        instances = this@UkurCepatApp

        boxStore =
                MyObjectBox.builder().androidContext(this@UkurCepatApp).name("ukur_cepatdb").build()
        val okHttpClient = OkHttpSingleton.getOkHttpClient()
        AndroidNetworking.initialize(this@UkurCepatApp.applicationContext, okHttpClient)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this@UkurCepatApp)
    }

    fun getBoxStoreInstances(): BoxStore {

        if (!this::boxStore.isInitialized) {
            boxStore = MyObjectBox.builder().androidContext(this@UkurCepatApp).name("ukur_cepatdb")
                .build()
        }
        return boxStore
    }
}