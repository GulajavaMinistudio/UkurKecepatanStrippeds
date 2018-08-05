package gulajava.speedcepat.dataparsers

import android.content.Context
import android.location.Location
import gulajava.speedcepat.models.KecepatanItems

/**
 * Created by Gulajava Ministudio on 6/10/18.
 */
class KecepatanParsers {

    private val mDataParsers: DataParsers
    private val mContext: Context

    constructor(context: Context) {
        this.mContext = context
        mDataParsers = DataParsers(mContext)
    }


    //HITUNG LAJU KECEPATAN KERETA API
    //HITUNG KECEPATAN RATA-RATA DENGAN V = S/T
    fun hitungKecepatanKendaraan(
        locationAwal: Location,
        locationAkhir: Location,
        longwaktumulaiMs: Long,
        longwaktuAkhirMs: Long
    ): KecepatanItems {

        val kecepatanItems = KecepatanItems()

        val floatjaraktempuh: Float
        val jaraktempuhstr: String

        val dowaktumulaims: Double
        val dowaktusekarangms: Double
        val dowaktuselisih: Double
        val dowaktuselisihdetik: Double
        val dojaraktempuh: Double
        var kecepatanvst = 0.0

        var dokecepatankmh = 0.0
        var dokecepatanmph = 0.0
        var dokecepatanknot = 0.0
        var intkecepatankmh = 0
        var intkecepatanmph = 0
        var intkecepatanknot = 0

        try {

            //hitung jarak tempuh
            floatjaraktempuh = locationAwal.distanceTo(locationAkhir)
            jaraktempuhstr = floatjaraktempuh.toString() + ""

            dowaktumulaims = longwaktumulaiMs.toString().toDoubleOrNull() ?: 0.0
            dowaktusekarangms = longwaktuAkhirMs.toString().toDoubleOrNull() ?: 0.0

            if (dowaktusekarangms > dowaktumulaims) {

                dowaktuselisih = dowaktusekarangms - dowaktumulaims
                dowaktuselisihdetik = dowaktuselisih / 1000

                dojaraktempuh = jaraktempuhstr.toDoubleOrNull() ?: 0.0

                //v = s/t  meter per detik
                kecepatanvst = dojaraktempuh / dowaktuselisihdetik

                dokecepatankmh = cariCepatanKmh(kecepatanvst)
                dokecepatanmph = cariCepatanMph(kecepatanvst)
                dokecepatanknot = cariCepatanKnot(kecepatanvst)

                intkecepatankmh =
                        mDataParsers.convertPembulatanBilangan(dokecepatankmh.toString(), 2)
                intkecepatanmph =
                        mDataParsers.convertPembulatanBilangan(dokecepatanmph.toString(), 2)
                intkecepatanknot =
                        mDataParsers.convertPembulatanBilangan(dokecepatanknot.toString(), 2)
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        kecepatanItems.mDoubleKecepatanMs = kecepatanvst
        kecepatanItems.mIntKecepatanKmh = intkecepatankmh
        kecepatanItems.mIntKecepatanMph = intkecepatanmph
        kecepatanItems.mIntKecepatanKnot = intkecepatanknot

        return kecepatanItems
    }


    //HITUNG DALAM SATUAN KM/JAM
    fun cariCepatanKmh(dokecepatandasar: Double): Double {

        var docepatkmh: Double
        try {
            if (dokecepatandasar != 0.0) {
                docepatkmh = dokecepatandasar * 3600 / 1000
            } else {
                docepatkmh = 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            docepatkmh = 0.0
        }

        return docepatkmh
    }


    //HITUNG DALAM KECEPATAN MPH
    fun cariCepatanMph(dokecepatandasar: Double): Double {

        var docepatmph: Double
        try {
            if (dokecepatandasar != 0.0) {
                docepatmph = dokecepatandasar * 2.23694
            } else {
                docepatmph = 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            docepatmph = 0.0
        }

        return docepatmph
    }

    // HITUNG KECEPATAN DALAM KNOT
    fun cariCepatanKnot(dokecepatandasar: Double): Double {

        var docepatknot: Double
        try {
            if (dokecepatandasar != 0.0) {
                docepatknot = dokecepatandasar * 1.9438
            } else {
                docepatknot = 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            docepatknot = 0.0
        }

        return docepatknot
    }

    fun convertKmhToMS(doubleKecepatanKMH: Double): Double {

        var doubleKecepatanMs = 0.0

        try {
            doubleKecepatanMs = doubleKecepatanKMH * 0.277778
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return doubleKecepatanMs
    }

    fun convertMphToMS(doubleKecepatanMph: Double): Double {

        var doubleKecepatanMs = 0.0

        try {
            doubleKecepatanMs = doubleKecepatanMph * 0.44704
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return doubleKecepatanMs
    }

    fun convertKnotToMS(doubleKecepatanKnot: Double): Double {

        var doubleKecepatanMs = 0.0

        try {
            doubleKecepatanMs = doubleKecepatanKnot * 0.514444
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return doubleKecepatanMs
    }
}