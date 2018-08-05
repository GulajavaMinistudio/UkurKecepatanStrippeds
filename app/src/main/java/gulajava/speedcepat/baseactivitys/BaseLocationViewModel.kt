package gulajava.speedcepat.baseactivitys

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest

/**
 * Created by Gulajava Ministudio on 6/1/18.
 */
class BaseLocationViewModel : AndroidViewModel {

    private val applicationApp: Application

    private lateinit var locationLiveData: LocationLiveData
    private lateinit var lastLocationLiveData: LastLocationLiveData

    constructor(application: Application) : super(application) {
        applicationApp = application
    }

    fun initLiveData(
        fusedLocationProviderClient: FusedLocationProviderClient,
        mLocationRequest: LocationRequest
    ) {

        if (!this::locationLiveData.isInitialized) {
            locationLiveData = LocationLiveData(
                applicationApp.applicationContext, fusedLocationProviderClient,
                mLocationRequest
            )
        }

        if (!this::lastLocationLiveData.isInitialized) {
            lastLocationLiveData = LastLocationLiveData(
                applicationApp.applicationContext, fusedLocationProviderClient,
                mLocationRequest
            )
        }
    }

    fun getLocationTrackLiveData(): LocationLiveData {
        return locationLiveData
    }

    fun getLastLocationLiveData(): LastLocationLiveData {
        return lastLocationLiveData
    }
}