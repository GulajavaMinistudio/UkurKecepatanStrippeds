package gulajava.speedcepat.mainmenus

import android.arch.lifecycle.LiveData
import android.location.Location
import gulajava.speedcepat.models.MsgHasilGeocoder
import gulajava.speedcepat.models.mainmenus.StatusGPSInternetData
import gulajava.speedcepat.states.StateData

/**
 * Created by Gulajava Ministudio on 5/14/18.
 */
interface MainMenuContract {

    interface View {

        fun initViewWidget()

        //INISIALISASI DATA AWAL
        fun initDataAwal()

        //INISIALISASI LISTENER
        fun initListener()

        //INISIALISASI TAMPILAN
        fun initTampilan()

        fun initViewModel()

        fun initViewModelObservers()

        fun stopViewModelObservers()

        fun checkStateLiveData(stateData: StateData)

        //CEK PERMISI LOKASI
        fun cekPermisiLokasiGPSLacakPosisi()

        //AMBIL LOKASI PENGGUNA DARI BUS
        fun ambilLokasiPenggunaDariBus()

        fun cekKoneksiInternetGPS()

        fun setStatusGPSKoneksiInternet(
            stringStatusGPS: String,
            stringStatusInternetKoneksi: String
        )

        //INISIALISASI MULAI HITUNG KECEPATAN
        fun startHitungKecepatan()

        //SETEL ALAMAT LOKASI PENGGUNA
        fun setelTeksLokasiPengguna(lokasiPengguna: String)

        fun tampilPesanPeringatan(resID: Int)
    }

    interface Presenter {

        // GET LIVE DATA
        fun getAlamatPenggunaLiveData(): LiveData<MsgHasilGeocoder>

        fun getStatusGPSInternetData(): LiveData<StatusGPSInternetData>

        fun getStateViewLiveData(): LiveData<StateData>

        //AKTIFKAN SUBSCRIBER
        fun initSubscriber()

        //HENTIKAN SUBSCRIBER
        fun stopSubscriber()

        fun restartSubscribers()

        // PUBLISH SUBJECT SUBSCRIBER
        fun initPublishSubjectSubscriber()

        fun cekStatusKoneksiGPSInternet()

        fun cekLokasiPengguna(location: Location)

        fun setelTeksLokasiPengguna(msgHasilGeocoder: MsgHasilGeocoder)

        fun cekStatusKoneksiGPSInternetPindahHalaman()

        //AMBIL GEOCODER PENGGUNA
        fun taskAmbilGeocoderPengguna()

        fun tampilPesanPeringatan(resID: Int)
    }
}