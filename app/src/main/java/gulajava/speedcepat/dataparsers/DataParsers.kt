package gulajava.speedcepat.dataparsers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import gulajava.speedcepat.models.MsgHasilGeocoder
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Gulajava Ministudio on 6/10/18.
 */
class DataParsers {

    private var mContext: Context
    private var mGeocoder: Geocoder? = null
    private var mAddressListPengguna: MutableList<Address>? = ArrayList()
    private var mStringGeocoderAlamat: String = ""
    private var mStringGeocoderNamaKota: String = ""
    private var mStringAlamatGabungan: String = ""

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    //AMBIL GEOCODER LOKASI
    fun getDataGeocoderLocation(latitude: String, longitude: String): MsgHasilGeocoder {

        mGeocoder = Geocoder(mContext, Locale.ENGLISH)
        var doubleLatitude = 0.0
        var doubleLongitude = 0.0

        try {
            doubleLatitude = latitude.toDoubleOrNull() ?: 0.0
            doubleLongitude = longitude.toDoubleOrNull() ?: 0.0
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        try {

            mAddressListPengguna = mGeocoder?.getFromLocation(doubleLatitude, doubleLongitude, 1) ?:
                    ArrayList()

            val panjangDataAlamat = mAddressListPengguna?.size ?: 0
            if (panjangDataAlamat > 0) {

                val panjangalamat = mAddressListPengguna?.get(0)?.maxAddressLineIndex ?: 0

                if (panjangalamat > 0) {

                    mStringGeocoderAlamat = mAddressListPengguna?.get(0)?.getAddressLine(0) ?: ""
                    mStringGeocoderNamaKota = mAddressListPengguna?.get(0)?.locality ?: ""

                } else {
                    mStringGeocoderAlamat = ""
                    mStringGeocoderNamaKota = ""
                    mStringAlamatGabungan = ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mStringGeocoderAlamat = ""
            mStringGeocoderNamaKota = ""
            mStringAlamatGabungan = ""
        }

        if (mStringGeocoderNamaKota.isNotEmpty()) {

            mStringAlamatGabungan = mStringGeocoderNamaKota
            if (mStringGeocoderAlamat.isNotEmpty()) {
                mStringAlamatGabungan = "$mStringGeocoderAlamat, $mStringGeocoderNamaKota"
            }
        } else {
            mStringAlamatGabungan = ""
        }

        val msgHasilGeocoder = MsgHasilGeocoder()
        msgHasilGeocoder.mStringAlamatGabungan = mStringAlamatGabungan

        return msgHasilGeocoder
    }


    //AMBIL GEOCODER LOKASI
    fun getDataGeocoderAddress(arraylistAlamat: ArrayList<Address>): MsgHasilGeocoder {

        try {
            mAddressListPengguna = arraylistAlamat

            val panjangDataAlamat = mAddressListPengguna?.size ?: 0
            if (panjangDataAlamat > 0) {

                val panjangalamat = mAddressListPengguna?.get(0)?.maxAddressLineIndex ?: 0

                if (panjangalamat > 0) {

                    mStringGeocoderAlamat = mAddressListPengguna?.get(0)?.getAddressLine(0) ?: ""
                    mStringGeocoderNamaKota = mAddressListPengguna?.get(0)?.locality ?: ""

                } else {
                    mStringGeocoderAlamat = ""
                    mStringGeocoderNamaKota = ""
                    mStringAlamatGabungan = ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mStringGeocoderAlamat = ""
            mStringGeocoderNamaKota = ""
            mStringAlamatGabungan = ""
        }

        if (mStringGeocoderNamaKota.isNotEmpty()) {

            mStringAlamatGabungan = mStringGeocoderNamaKota
            if (mStringGeocoderAlamat.isNotEmpty()) {
                mStringAlamatGabungan = "$mStringGeocoderAlamat, $mStringGeocoderNamaKota"
            }
        } else {
            mStringAlamatGabungan = ""
        }

        val msgHasilGeocoder = MsgHasilGeocoder()
        msgHasilGeocoder.mStringAlamatGabungan = mStringAlamatGabungan

        return msgHasilGeocoder
    }

    //KONVERSI PEMBULATAN KE KILOMETER
    fun convertKilometerToInteger(strnilaiawal: String, pembulatan: Int): Int {

        val donilaimeter: Double = strnilaiawal.toDoubleOrNull() ?: 0.0
        val donilaikm = donilaimeter / 1000

        var bilanganhasil = 0
        var bigdesimal: BigDecimal

        try {
            bigdesimal = BigDecimal(donilaikm)
            bigdesimal = bigdesimal.setScale(pembulatan, RoundingMode.DOWN)
            bilanganhasil = bigdesimal.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bilanganhasil
    }

    //PEMBULATAN BILANGAN DIBULATKAN KE ATAS
    fun convertPembulatanBilanganKeAtas(stringNilaiAwal: String, intJumlahPembulatan: Int): Int {

        var bilanganHasil = 0
        var bigDecimal: BigDecimal

        try {
            val doubleValBelumBulat = stringNilaiAwal.toDoubleOrNull() ?: 0.0
            bigDecimal = BigDecimal(doubleValBelumBulat)
            bigDecimal = bigDecimal.setScale(intJumlahPembulatan, RoundingMode.UP)
            bilanganHasil = bigDecimal.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bilanganHasil
    }

    //PEMBULATAN JARAK HITUNG
    //FUNGSI PEMBULAT ANGKA
    fun convertPembulatanBilangan(stringNilaiAwal: String, intJumlahPembulatan: Int): Int {

        var bilanganHasil = 0
        var bigDecimal: BigDecimal

        try {
            val doubleValBelumBulat = stringNilaiAwal.toDoubleOrNull() ?: 0.0
            bigDecimal = BigDecimal(doubleValBelumBulat)
            bigDecimal = bigDecimal.setScale(intJumlahPembulatan, RoundingMode.UP)
            bilanganHasil = bigDecimal.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bilanganHasil
    }
}