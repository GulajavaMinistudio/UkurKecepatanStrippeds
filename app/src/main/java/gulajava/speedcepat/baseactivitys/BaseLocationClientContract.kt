package gulajava.speedcepat.baseactivitys

import android.location.Location

/**
 * Created by Gulajava Ministudio on 5/14/18.
 */
interface BaseLocationClientContract {

    //AMBIL LOKASI DAN SET LOKASI
    fun getLocation(): Location?

    fun setLokasi(location: Location)

    //INISIALISASI DATA AWAL
    fun initDataAwalLokasi()

    fun initDataAwalLokasiTrackKecepatan()

    //INISIALISASI GOOGLE LOCATION CLIENT LISTENER
    fun initListenerLokasi()

    // INISIALISASI VIEW MODEL LIVE DATA
    fun initViewModelDataLokasi()

    //AMBIL IJIN LOKASI SAJA TANPA UPDATE LOKASI
    fun cekPermissionLokasiGPSOnly()

    //AMBIL IJIN LOKASI DENGAN UPDATE LOKASI
    fun cekPermissionLokasiGPSTrack()

    //CEK SETELAN LOKASI PERANGKAT
    fun cekSetelanGPSPerangkat()

    //CEK STATUS GPS LOKASI
    fun cekStatusGPSAktif()

    //CEK STATUS INTERNET
    fun cekStatusInternet()

    //CEK LOKASI TERAKHIR PENGGUNA
    fun cekLokasiTerakhir()

    //AKTIFKAN SERVICE TRACK LOKASI
    fun aktifkanGPSTrackLokasi()

    //HENTIKAN SERVICE TRACK LOKASI
    fun stopGPSTrackLokasi()

    //TAMPIL DIALOG GAGAL LOKASI
    fun tampilDialogLokasiGagal()

    fun kirimPesanBusLokasiJalan(kodePesan: Int)

    // INTERFACE LOKASI JIKA TERDAPAT UPDATE
    interface OnLocationUpdatesListener {
        fun onLocationUpdates(location: Location)
    }

    fun setListenerLokasiBase(onLocationListener: OnLocationUpdatesListener)

    fun showToastPeringatan(resID: Int)
}