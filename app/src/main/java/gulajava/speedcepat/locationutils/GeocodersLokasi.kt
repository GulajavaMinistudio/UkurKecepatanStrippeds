package gulajava.speedcepat.locationutils

import android.location.Address
import android.util.Log
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Gulajava Ministudio on 6/19/18.
 */
class GeocodersLokasi {

    val TAG = GeocodersLokasi::class.java.simpleName

    var client = OkHttpClient()

    fun getFromLocation(lat: Double, lng: Double, maxResult: Int): ArrayList<Address> {

        val address = String.format(
            Locale.US,
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1\$f,%2\$f&sensor=false&language=" + Locale.getDefault().country,
            lat,
            lng
        )
        Log.d(TAG, "address = $address")
        Log.d(TAG, "Locale.getDefault().getCountry() = " + Locale.getDefault().country)

        return getAddress(address, maxResult)

    }

    fun getFromUrlsLocation(lat: Double, lng: Double): String {

        val address = String.format(
            Locale.US,
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1\$f,%2\$f&sensor=false&language=" + Locale.getDefault().country,
            lat,
            lng
        )
        Log.d(TAG, "address = $address")
        Log.d(TAG, "Locale.getDefault().getCountry() = " + Locale.getDefault().country)

        return address

    }

    fun getFromLocationName(locationName: String, maxResults: Int): ArrayList<Address> {

        val address: String
        try {
            address = "https://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(
                locationName,
                "UTF-8"
            ) + "&ka&sensor=false"
            return getAddress(address, maxResults)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ArrayList()
    }

    private fun getAddress(url: String, maxResult: Int): ArrayList<Address> {

        var retList: ArrayList<Address> = ArrayList()

        val request = Request.Builder().url(url)
            .header("User-Agent", "OkHttp Headers.java")
            .addHeader("Accept", "application/json; q=0.5")
            .build()
        try {
            val response = client.newCall(request).execute()
            val responseStr = response.body()?.string() ?: ""
            val jsonObject = JSONObject(responseStr)

            retList = ArrayList()

            if ("OK".equals(jsonObject.getString("status"), ignoreCase = true)) {
                val results = jsonObject.getJSONArray("results")
                if (results.length() > 0) {
                    var i = 0
                    while (i < results.length() && i < maxResult) {
                        val result = results.getJSONObject(i)
                        val addr = Address(Locale.getDefault())

                        val components = result.getJSONArray("address_components")
                        var streetNumber = ""
                        var route = ""
                        for (a in 0 until components.length()) {
                            val component = components.getJSONObject(a)
                            val types = component.getJSONArray("types")
                            for (j in 0 until types.length()) {
                                val type = types.getString(j)
                                if (type == "locality") {
                                    addr.locality = component.getString("long_name")
                                } else if (type == "street_number") {
                                    streetNumber = component.getString("long_name")
                                } else if (type == "route") {
                                    route = component.getString("long_name")
                                }
                            }
                        }
                        addr.setAddressLine(0, "$route $streetNumber")

                        addr.latitude = result.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat")
                        addr.longitude = result.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng")
                        retList.add(addr)
                        i++
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Google geocode webservice.", e)
        }

        return retList
    }

    fun getAddressObservables(url: String, maxResult: Int): Observable<ArrayList<Address>> {

        val observable: Observable<ArrayList<Address>> = Observable.fromCallable {

            var retList: ArrayList<Address> = ArrayList()

            val request = Request.Builder().url(url)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseStr = response.body()?.string() ?: ""
                val jsonObject = JSONObject(responseStr)

                Log.w("JSON HASIL", "hasil " + responseStr)
                retList = ArrayList()

                if ("OK".equals(jsonObject.getString("status"), ignoreCase = true)) {
                    val results = jsonObject.getJSONArray("results")
                    if (results.length() > 0) {
                        var i = 0
                        while (i < results.length() && i < maxResult) {
                            val result = results.getJSONObject(i)
                            val addr = Address(Locale.getDefault())

                            val components = result.getJSONArray("address_components")
                            var streetNumber = ""
                            var route = ""
                            for (a in 0 until components.length()) {
                                val component = components.getJSONObject(a)
                                val types = component.getJSONArray("types")
                                for (j in 0 until types.length()) {
                                    val type = types.getString(j)
                                    if (type == "locality") {
                                        addr.locality = component.getString("long_name")
                                    } else if (type == "street_number") {
                                        streetNumber = component.getString("long_name")
                                    } else if (type == "route") {
                                        route = component.getString("long_name")
                                    }
                                }
                            }
                            addr.setAddressLine(0, "$route $streetNumber")

                            addr.latitude =
                                    result.getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lat")
                            addr.longitude =
                                    result.getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lng")
                            retList.add(addr)
                            i++
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calling Google geocode webservice.", e)
            }

            return@fromCallable retList
        }

        return observable
    }
}