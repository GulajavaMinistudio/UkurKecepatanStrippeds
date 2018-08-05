package gulajava.speedcepat

import android.Manifest

/**
 * Created by Gulajava Ministudio on 4/30/18.
 */
class Konstans {

    companion object {

        val STR_KMH: String = "km/jam"
        val STR_MPH: String = "mil/jam"
        val STR_KNT: String = "knot"

        val DOUBLE_BATAS_BAWAH_KMH: Double = 10.toDouble()
        val DOUBLE_BATAS_ATAS_KMH: Double = 110.toDouble()

        val DOUBLE_BATAS_BAWAH_MPH: Double = 6.toDouble()
        val DOUBLE_BATAS_ATAS_MPH: Double = 68.toDouble()

        val DOUBLE_BATAS_BAWAH_KNOT: Double = 5.toDouble()
        val DOUBLE_BATAS_ATAS_KNOT: Double = 64.toDouble()

        //PERMISI LOKASI
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val KODE_LOKASIAWAL = 100
        val KODE_LOKASIBARU = 101
        val KODE_IJINLOKASIOK = 70
        val KODE_IJINLOKASIGAGAL = 71
        const val REQUEST_CODE_LOCATION_TRACK = 40
        const val REQUEST_CODE_PERMISILOKASI = 41
        val TAG_INTENT_REQUESTLOKASI = "kodereqs"

        // request code check settings
        val REQUEST_CHECK_SETTINGS = 0x1

        val ID_NOTIFIKASI_BATAS_KECEPATAN = 7
        val ID_CHANNEL_NOTIFICATION_KECEPATAN = "kecepatanukuran_service_push"
        val ID_NAME_CHANNEL_NOTIFICATION_KECEPATAN = "Pengukur_Kecepatan_Kendaraan"
    }
}