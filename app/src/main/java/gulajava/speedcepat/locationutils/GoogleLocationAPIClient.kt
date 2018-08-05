package gulajava.jdwl.locations

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

/**
 * Created by Gulajava Ministudio on 12/29/17.
 */
class GoogleLocationAPIClient {

    companion object {

        /**
         * Ambil Google API Client untuk layanan lokasi
         */
        @Synchronized
        fun getLocationClient(
            context: Context,
            connectionCallbacks: GoogleApiClient.ConnectionCallbacks,
            connectionFailedListener: GoogleApiClient.OnConnectionFailedListener
        ): GoogleApiClient {

            val googleBuilder =
                GoogleApiClient.Builder(context).addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API)

            return googleBuilder.build()
        }

        /**
         * Ambil location request berdasarkan jaringan BTS dan seluler
         */
        fun getLocationNetworkReq(): LocationRequest {

            val locationRequest = LocationRequest()
            locationRequest.interval = 60000   // 1 menit
            locationRequest.fastestInterval = 30000 //30 detik
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            locationRequest.smallestDisplacement = 5f

            return locationRequest
        }


        /**
         * Ambil location request untuk metode akurasi dengan GPS
         */
        fun getLocationGPSReq(): LocationRequest {

            val locationRequest = LocationRequest()
            locationRequest.interval = 15000  //15 detik
            locationRequest.fastestInterval = 10000 //10 detik
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.smallestDisplacement = 10f // perubahan 10 meter baru update lokasi

            return locationRequest
        }

        /**
         * Ambil location request untuk metode akurasi dengan GPS yang sangat akurat
         */
        fun getLocationGPSReqKecepatanAkurasi(): LocationRequest {

            val locationRequest = LocationRequest()
            locationRequest.interval = 10000  //10 detik
            locationRequest.fastestInterval = 5000 //5 detik
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            return locationRequest
        }


        /**
         * ===============  UNTUK SERVICE DI BELAKANG LAYAR PANTAU LOKASI =============
         */

        /**
         * Ambil location request berdasarkan jaringan network gsm
         */
        fun getLocationNetworkReqService(): LocationRequest {

            val locationRequest = LocationRequest()
            locationRequest.interval = 15000  //15 detik
            locationRequest.fastestInterval = 8000 //8 detik
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            locationRequest.smallestDisplacement = 10f //di update setiap  10 meter sekali

            return locationRequest
        }

        /**
         * Ambil location request berdasarkan akurasi jaringan gps
         */
        fun getLocationGPSReqService(): LocationRequest {

            val locationRequest = LocationRequest()
            locationRequest.interval = 25000  //25 detik
            locationRequest.fastestInterval = 15000  //15 detik
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.smallestDisplacement = 15f  //di update setiap 15 meter sekali

            return locationRequest
        }
    }
}