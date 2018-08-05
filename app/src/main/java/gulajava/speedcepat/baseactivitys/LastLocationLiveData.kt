package gulajava.speedcepat.baseactivitys

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task

/**
 * Created by Gulajava Ministudio on 5/14/18.
 */
class LastLocationLiveData : LiveData<Location> {

    private val mContext: Context
    private val mFusedLocationProviderClient: FusedLocationProviderClient
    private val mLocationRequestGPSTrack: LocationRequest
    private lateinit var mLocationCallback: LocationCallback

    constructor(
        mContext: Context,
        mFusedLocationProviderClient: FusedLocationProviderClient,
        mLocationRequestGPSTrack: LocationRequest
    ) : super() {
        this.mContext = mContext
        this.mFusedLocationProviderClient = mFusedLocationProviderClient
        this.mLocationRequestGPSTrack = mLocationRequestGPSTrack

        initCallBackLocationRequest()
    }


    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                val location: Location = task.result
                this@LastLocationLiveData.postValue(location)
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
    }

    fun initCallBackLocationRequest() {

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                locationResult?.let { locationresult ->

                    val location: Location? = locationresult.lastLocation
                    if (location != null) {
                        this@LastLocationLiveData.postValue(location)
                    }
                }
            }
        }
    }
}