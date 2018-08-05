package gulajava.speedcepat.hitungkecepatan

import android.arch.lifecycle.LiveData
import android.location.Location
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.models.KecepatanItems
import gulajava.speedcepat.models.MsgHasilGeocoder
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKecepatanBatas

/**
 * Created by Gulajava Ministudio on 6/19/18.
 */
interface HitungKecepatanContract {

    interface View {

        fun initViewWidget()

        //INISIALISASI DATA AWAL
        fun initDataAwal()

        //INISIALISASI LISTENER
        fun initListener()

        //INISIALISASI TAMPILAN
        fun initTampilan()

        fun initViewModelObservers()

        fun stopViewModelObservers()

        fun checkStateLiveData(stateData: StateData)

        fun setSelectedBottomNavigation(intIdBottomNav: Int)

        fun setSelectedViewPager(intPosisi: Int)

        fun getDataSetelanBatasKecepatan()

        // CEK PERMISI LOKASI
        fun cekPermisiLokasiGPSLacakPosisi()

        // AMBIL LOKASI PENGGUNA DARI BUS
        // MULAI HITUNG KECEPATAN UNTUK DIKIRIM KE FRAGMENT
        fun startHitungKecepatan()

        // TAMPILKAN PENGINGAT KECEPATAN
        fun showStatusBatasKecepatan()

        fun sembunyikanStatusBatasKecepatan()

        fun showDialogBantuan()

        fun createNotificationChannel(): String

        fun tampilPesanPeringatan(resID: Int)
    }

    interface Presenter {

        // LIVE DATA
        fun getLiveDataAlamatLokasiPengguna(): LiveData<MsgHasilGeocoder>

        fun getLiveDataNilaiKecepatan(): LiveData<KecepatanItems>

        fun getLiveDataDbSetelan(): LiveData<DbSetelan>

        fun getLiveDataStateBatasKecepatan(): LiveData<StateKecepatanBatas>

        fun getLiveDataStateView(): LiveData<StateData>

        //AKTIFKAN SUBSCRIBER
        fun initSubscriber()

        //HENTIKAN SUBSCRIBER
        fun stopSubscriber()

        fun restartSubscribers()

        // PUBLISH SUBJECT SUBSCRIBER
        fun initPublishSubjectSubscriber()

        fun cekStatusKoneksiGPSInternet()

        fun cekLokasiPengguna(location: Location)

        fun cekLokasiPenggunaUpdate(location: Location)

        fun setelTeksLokasiPengguna(msgHasilGeocoder: MsgHasilGeocoder)

        //AMBIL GEOCODER PENGGUNA
        fun startTaskAmbilGeocoderPengguna()

        fun hitungKecepatanKendaraan()

        fun getDataBatasKecepatanDb()

        fun setDataBatasKecepatanDb(dbSetelan: DbSetelan)

        fun cekBatasKecepatanKendaraan()

        fun sendNotifikasiBatasKecepatan(isKecepatanLewatBatas: Boolean)

        fun stopHitungKecepatan()

        fun tampilPesanPeringatan(resID: Int)
    }
}