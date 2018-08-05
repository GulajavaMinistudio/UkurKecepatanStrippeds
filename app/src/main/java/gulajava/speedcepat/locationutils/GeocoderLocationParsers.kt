package gulajava.speedcepat.locationutils

import android.location.Address
import android.util.Log
import com.rx2androidnetworking.Rx2AndroidNetworking
import gulajava.speedcepat.internets.models.*
import gulajava.speedcepat.models.MsgHasilGeocoder
import io.reactivex.Observable
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
class GeocoderLocationParsers {

    private val TAG = GeocoderLocationParsers::class.java.simpleName

    private var mStringGeocoderAlamat: String = ""
    private var mStringGeocoderNamaKota: String = ""
    private var mAddressListPengguna: ArrayList<Address> = ArrayList()


    fun getFromUrlsLocation(lat: Double, lng: Double): String {

        val address = String.format(
            Locale.US,
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1\$f,%2\$f&sensor=false&language="
                    + Locale.getDefault().country,
            lat,
            lng
        )

        Log.d(TAG, "address = $address")
        Log.d(TAG, "Locale.getDefault().getCountry() = " + Locale.getDefault().country)

        return address
    }

    fun getFromLocationName(locationName: String, maxResults: Int): String {

        var address = ""
        try {
            address = "https://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(
                locationName,
                "UTF-8"
            ) + "&ka&sensor=false"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return address
    }

    fun parseJsonDataLokasiGeocoder(geocodingLocationModel: GeocodingLocationModel): ArrayList<Address> {

        val addressList: ArrayList<Address> = ArrayList()

        try {
            val addressResultList: ArrayList<AddressResultItem> =
                geocodingLocationModel.mListAddressResult

            for (addressItem: AddressResultItem in addressResultList) {

                val address = Address(Locale.getDefault())

                val stringAlamatLengkap: String = addressItem.mStringFormattedAddress
                address.setAddressLine(0, stringAlamatLengkap)

                val typesAlamat: ArrayList<String> = addressItem.mListTypeLokasi

                // cek tipe alamat yang disimpan
                for (stringTipe: String in typesAlamat) {

                    if (stringTipe.contentEquals("locality")) {
                        address.locality = addressItem.mStringFormattedAddress
                    } else if (stringTipe.contentEquals("postal_code")) {

                        // cek komponen alamat di dalam array
                        val addressComponentsList: ArrayList<AddressComponentItem> =
                            addressItem.mAddressComponentItemList
                        for (addressComponentItem: AddressComponentItem in addressComponentsList) {

                            val typesAddressComponentItemList: ArrayList<String> =
                                addressComponentItem.mListTypes
                            val stringLongNameAddressComponent: String =
                                addressComponentItem.mStringLongName
                            for (stringTipeAddressComponent: String in typesAddressComponentItemList) {

                                if (stringTipeAddressComponent.contentEquals("postal_code")) {
                                    address.postalCode = stringLongNameAddressComponent
                                }
                            }
                        }
                    } else if (stringTipe.contentEquals("country")) {
                        address.countryName = addressItem.mStringFormattedAddress
                    }
                }

                // ambil koordinat latitude longitude
                val geometryLokasi: GeometryItem = addressItem.mGeometryItem
                val locationItem: LocationItem = geometryLokasi.mLocation

                val stringLatitude: String = locationItem.mStringLatitude
                val doubleLatitude: Double = stringLatitude.toDoubleOrNull() ?: 0.0
                val stringLongitude: String = locationItem.mStringLongitude
                val doubleLongitude: Double = stringLongitude.toDoubleOrNull() ?: 0.0

                address.latitude = doubleLatitude
                address.longitude = doubleLongitude

                // masukkan ke dalam array
                addressList.add(address)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return addressList
    }


    //AMBIL GEOCODER LOKASI
    fun getDataGeocoderAddress(arraylistAlamat: ArrayList<Address>): MsgHasilGeocoder {

        try {
            mAddressListPengguna = arraylistAlamat

            val panjangDataAlamat = mAddressListPengguna.size

            if (panjangDataAlamat > 0) {

                val address: Address = mAddressListPengguna[0]
                mStringGeocoderAlamat = address.getAddressLine(0) ?: ""
                mStringGeocoderNamaKota = address.locality ?: ""
            } else {
                mStringGeocoderAlamat = ""
                mStringGeocoderNamaKota = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mStringGeocoderAlamat = ""
            mStringGeocoderNamaKota = ""
        }

        if (mStringGeocoderAlamat.isEmpty()) {
            if (mStringGeocoderNamaKota.isNotEmpty()) {
                mStringGeocoderAlamat = mStringGeocoderNamaKota
            }
        }

        val msgHasilGeocoder = MsgHasilGeocoder()
        msgHasilGeocoder.mStringAlamatGabungan = mStringGeocoderAlamat

        return msgHasilGeocoder
    }


    fun getObservableRequestGeocoders(stringUrl: String): Observable<GeocodingLocationModel> {

        val observable: Observable<GeocodingLocationModel> = Rx2AndroidNetworking.get(stringUrl)
            .addHeaders("User-Agent", "OkHttp Headers.java")
            .addHeaders("Accept", "application/json; q=0.5")
            .build()
            .getObjectObservable(GeocodingLocationModel::class.java)

        return observable
    }


}