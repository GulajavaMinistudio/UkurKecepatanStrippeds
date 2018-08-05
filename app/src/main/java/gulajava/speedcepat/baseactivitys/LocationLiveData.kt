package gulajava.speedcepat.baseactivitys

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

/**
 * Created by Gulajava Ministudio on 5/14/18.
 */
class LocationLiveData : LiveData<Location> {

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
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequestGPSTrack,
            mLocationCallback,
            Looper.myLooper()
        )
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
                        this@LocationLiveData.postValue(location)
                    }
                }
            }
        }
    }
}