package gulajava.speedcepat.mainmenus

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.Address
import android.location.Location
import gulajava.jdwl.locations.CekGPSNet
import gulajava.speedcepat.R
import gulajava.speedcepat.internets.models.GeocodingLocationModel
import gulajava.speedcepat.locationutils.GeocoderLocationParsers
import gulajava.speedcepat.models.MsgHasilGeocoder
import gulajava.speedcepat.models.mainmenus.StatusGPSInternetData
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKonstans
import gulajava.speedcepat.dataparsers.DataParsers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Gulajava Ministudio on 5/14/18.
 */
class MainMenuViewModel : AndroidViewModel, MainMenuContract.Presenter {

    private val mApplication: Application

    private lateinit var mCompositeDisposable: CompositeDisposable

    //ambil lokasi pengguna
    private var mDoubleLatitude = 0.0
    private var mDoubleLongitude = 0.0

    private var mLocationPengguna: Location = Location("")

    private var isInternet: Boolean = false
    private var isNetworkNyala: Boolean = false
    private var isNetworkGPS: Boolean = false
    private var mCekGPSNet: CekGPSNet? = null

    private var mStringAktif: String = ""
    private var mStringBelumAktif: String = ""
    private var mStringStatusGPS: String = ""
    private var mStringStatusKoneksiInternet: String = ""

    private val mDataParsers: DataParsers
    private val mGeocodersLokasi: GeocoderLocationParsers

    private var mPublishSubjectHitungGeocoder: PublishSubject<Boolean> = PublishSubject.create()

    // live data
    private val mStringAlamatPenggunaLiveData: MutableLiveData<MsgHasilGeocoder> = MutableLiveData()
    private val mStatusGPSNetLiveData: MutableLiveData<StatusGPSInternetData> = MutableLiveData()
    private val mStatesViewLiveData: MutableLiveData<StateData> = MutableLiveData()

    constructor(application: Application) : super(application) {

        mApplication = application
        val mContext = application.applicationContext
        mDataParsers = DataParsers(mContext)
        mGeocodersLokasi = GeocoderLocationParsers()

        mStringAktif = mContext.resources?.getString(R.string.kartustat_sudahaktif) ?: ""
        mStringBelumAktif = mContext.resources?.getString(R.string.kartustat_belumaktif) ?: ""

        mStringStatusGPS = mStringBelumAktif
        mStringStatusKoneksiInternet = mStringBelumAktif

        initSubscriber()
    }

    override fun getAlamatPenggunaLiveData(): LiveData<MsgHasilGeocoder> {

        return mStringAlamatPenggunaLiveData
    }


    override fun getStatusGPSInternetData(): LiveData<StatusGPSInternetData> {

        return mStatusGPSNetLiveData
    }

    override fun getStateViewLiveData(): LiveData<StateData> {

        return mStatesViewLiveData
    }

    override fun initSubscriber() {

        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        } else {
            if (mCompositeDisposable.isDisposed) {
                mCompositeDisposable = CompositeDisposable()
            }
        }

        initPublishSubjectSubscriber()
    }

    override fun stopSubscriber() {

        mCompositeDisposable.dispose()
    }

    override fun onCleared() {
        super.onCleared()

        stopSubscriber()
    }

    override fun restartSubscribers() {

        mCompositeDisposable.dispose()
        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        } else {
            if (mCompositeDisposable.isDisposed) {
                mCompositeDisposable = CompositeDisposable()
            }
        }

        initPublishSubjectSubscriber()
    }

    override fun initPublishSubjectSubscriber() {

        mCompositeDisposable.add(
            mPublishSubjectHitungGeocoder
                .observeOn(Schedulers.io())
                .map { _: Boolean ->

                    return@map mGeocodersLokasi.getFromUrlsLocation(
                        mDoubleLatitude,
                        mDoubleLongitude
                    )
                }
                .flatMap { stringurl: String ->

                    return@flatMap mGeocodersLokasi.getObservableRequestGeocoders(stringurl)
                }
                .map { geocodingmodel: GeocodingLocationModel ->

                    return@map mGeocodersLokasi.parseJsonDataLokasiGeocoder(geocodingmodel)
                }
                .map { arraylistalamat: ArrayList<Address> ->

                    return@map mGeocodersLokasi.getDataGeocoderAddress(arraylistalamat)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { msghasilgeocoder: MsgHasilGeocoder? ->
                        val alamatGabungan: String = msghasilgeocoder?.mStringAlamatGabungan ?: ""
                        if (alamatGabungan.isNotEmpty() && msghasilgeocoder != null) {
                            // setel posisi gps pengguna
                            setelTeksLokasiPengguna(msghasilgeocoder)
                        }
                    },
                    { error: Throwable? ->
                        error?.printStackTrace()
                        restartSubscribers()
                    }
                )
        )
    }


    override fun cekStatusKoneksiGPSInternet() {

        val context: Context? = mApplication.applicationContext
        mCekGPSNet = context?.let { CekGPSNet(it) }

        isInternet = mCekGPSNet?.cekStatusInternet() ?: false
        isNetworkNyala = mCekGPSNet?.cekStatusNetworkGSM() ?: false
        isNetworkGPS = mCekGPSNet?.cekStatusNetwork() ?: false

        if (isInternet && isNetworkGPS || isNetworkNyala && isNetworkGPS) {
            // cek posisi terakhir pengguna
            // jalankan proses update lokasi
            mStringStatusGPS = mStringAktif
        } else {
            mStringStatusGPS = mStringBelumAktif
        }

        val isJaringanSambung: Boolean =
            mCekGPSNet?.getKondisiNetwork(isInternet, isNetworkNyala) ?: false
        if (isJaringanSambung) {
            mStringStatusKoneksiInternet = mStringAktif
        } else {
            mStringStatusKoneksiInternet = mStringBelumAktif
        }

        val statusGPSInternetData = StatusGPSInternetData()
        statusGPSInternetData.stringStatusGPS = mStringStatusGPS
        statusGPSInternetData.stringStatusKoneksiInternet = mStringStatusKoneksiInternet

        mStatusGPSNetLiveData.postValue(statusGPSInternetData)
    }

    override fun cekLokasiPengguna(location: Location) {

        mLocationPengguna = location
        mDoubleLatitude = mLocationPengguna.latitude
        mDoubleLongitude = mLocationPengguna.longitude

        val lokasiPenggunaString = mDoubleLatitude.toString() + ", " + mDoubleLongitude.toString()
        val msgHasilGeocoder = MsgHasilGeocoder()
        msgHasilGeocoder.mStringAlamatGabungan = lokasiPenggunaString
        setelTeksLokasiPengguna(msgHasilGeocoder)

        taskAmbilGeocoderPengguna()
    }

    override fun setelTeksLokasiPengguna(msgHasilGeocoder: MsgHasilGeocoder) {

        mStringAlamatPenggunaLiveData.postValue(msgHasilGeocoder)
    }

    override fun cekStatusKoneksiGPSInternetPindahHalaman() {

        if (mStringStatusGPS.contentEquals(mStringAktif) && mStringStatusKoneksiInternet.contentEquals(
                mStringAktif
            )) {
            val stateData = StateData()
            stateData.intKodeState = StateKonstans.STATE_PINDAH_HALAMAN_UKURKECEPATAN
            mStatesViewLiveData.postValue(stateData)
        } else {
            tampilPesanPeringatan(R.string.toast_gagal_gps_internet_nonaktif)
        }
    }

    override fun taskAmbilGeocoderPengguna() {

        if (mPublishSubjectHitungGeocoder.hasObservers()) {
            mPublishSubjectHitungGeocoder.onNext(true)
        }
    }

    override fun tampilPesanPeringatan(resID: Int) {

        val stateData = StateData()
        stateData.intKodeState = StateKonstans.STATE_SHOW_TOAST
        stateData.intPayloadData = resID

        mStatesViewLiveData.value = stateData
    }
}