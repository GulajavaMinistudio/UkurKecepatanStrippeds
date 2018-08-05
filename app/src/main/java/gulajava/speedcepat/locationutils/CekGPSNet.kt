package gulajava.jdwl.locations

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager

/**
 * Created by Gulajava Ministudio on 12/29/17.
 */
class CekGPSNet(private val mContext: Context?) {

    private var mLocationManager: LocationManager? = null
    private var mConnectivityManager: ConnectivityManager? = null
    private var mTelephonyManager: TelephonyManager? = null
    private var mNetworkInfo: NetworkInfo? = null

    private var isInternet = false
    private var isNetworkNyala = false
    private var isGPSNyala = false

    private var statusNetwork = false
    private var statusGPS = false

    private var networkTipe: Int = TelephonyManager.NETWORK_TYPE_UNKNOWN
    private var networkOperator: String = ""

    init {

        isInternet = false
        isNetworkNyala = false
        isGPSNyala = false

        statusGPS = false
        statusNetwork = false

        mLocationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        mConnectivityManager =
                mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        mTelephonyManager =
                mContext?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    }

    /**
     * Cek status internet apakah aktif atau tidak
     * @return Boolean nilai balikan true jika aktif, false jika tidak ada
     */
    fun cekStatusInternet(): Boolean {

        isInternet = false
        mNetworkInfo = mConnectivityManager?.activeNetworkInfo
        mNetworkInfo?.let {
            isInternet = it.isConnected
        }
        return isInternet
    }

    /**
     * cek status network apakah tersambung dengan jaringan gsm seluler atau tidak
     */
    fun cekStatusNetworkGSM(): Boolean {

        try {
            networkTipe = mTelephonyManager?.networkType ?: TelephonyManager.NETWORK_TYPE_UNKNOWN
            networkOperator = mTelephonyManager?.networkOperator ?: ""
            isNetworkNyala = networkOperator.isNotEmpty() && networkTipe !=
                    TelephonyManager.NETWORK_TYPE_UNKNOWN
        } catch (e: Exception) {
            e.printStackTrace()
            isNetworkNyala = false
        }

        return isNetworkNyala
    }

    /**
     * Cek apakah status jaringan gps gsm hidup atau tidak, dan bisa diambil location dari sana atau tidak
     */
    fun cekStatusNetwork(): Boolean {

        isNetworkNyala = try {
            mLocationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }

        return isNetworkNyala
    }


    //  CEK APAKAH STATUS GPS HIDUP/TIDAK, DAN BISA DIAMBIL LOKASI LAT LONG NYA
    /**
     * Cek apakah status GPS aktif atau tidak, dan bisa diambil lokasinya atau tidak
     */
    fun cekStatusGPS(): Boolean {

        isGPSNyala = try {
            mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }

        return isGPSNyala
    }

    //  SETEL STATUS JARINGAN GSM DAN INTERNET
    /**
     * Setel status aktif dari jaringan gsm dan internet apakah tersambung atau tidak
     */
    fun getKondisiNetwork(isInternetNyambung: Boolean, isJaringanNyambung: Boolean): Boolean {

        statusNetwork = if (isInternetNyambung && isJaringanNyambung) {
            true
        } else if (!isInternetNyambung && !isJaringanNyambung) {
            false
        } else {
            false
        }

        return statusNetwork
    }

    /**
     * Setel
     */
    fun getKondisiGPS(isGPSnyambungs: Boolean): Boolean {

        statusGPS = isGPSnyambungs
        return statusGPS
    }
}