package gulajava.speedcepat.hitungkecepatan

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.Address
import android.location.Location
import gulajava.jdwl.locations.CekGPSNet
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.database.repositorys.RepoHitungKecepatan
import gulajava.speedcepat.internets.models.GeocodingLocationModel
import gulajava.speedcepat.locationutils.GeocoderLocationParsers
import gulajava.speedcepat.models.KecepatanItems
import gulajava.speedcepat.models.MsgHasilGeocoder
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKecepatanBatas
import gulajava.speedcepat.states.StateKonstans
import gulajava.speedcepat.dataparsers.DataParsers
import gulajava.speedcepat.dataparsers.KecepatanParsers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.joda.time.DateTime

/**
 * Created by Gulajava Ministudio on 6/23/18.
 */
class HitungKecepatanViewModel : AndroidViewModel, HitungKecepatanContract.Presenter {

    private val mApplication: Application
    private val mKecepatanParsers: KecepatanParsers
    private val mDataParsers: DataParsers
    private val mGeocodersLokasi: GeocoderLocationParsers

    private val mRepoHitungKecepatan: RepoHitungKecepatan =
        RepoHitungKecepatan(this@HitungKecepatanViewModel)

    private var mDbSetelan: DbSetelan = DbSetelan()

    //ambil lokasi pengguna
    private var mDoubleLatitude = 0.0
    private var mDoubleLongitude = 0.0

    private var mLocationPengguna: Location = Location("")
    private var mLocationAwal: Location = Location("")
    private var mLocationAkhir: Location = Location("")
    private var mLongWaktuAwal: Long = 0
    private var mLongWaktuAkhir: Long = 0


    private var isInternet: Boolean = false
    private var isNetworkNyala: Boolean = false
    private var isNetworkGPS: Boolean = false
    private var mCekGPSNet: CekGPSNet? = null
    private var isInitPengukurOK: Boolean = false

    private var mKecepatanItemsPengguna: KecepatanItems = KecepatanItems()

    private lateinit var mCompositeDisposable: CompositeDisposable
    private var mPublishSubjectHitungGeocoder: PublishSubject<Boolean> = PublishSubject.create()
    private var mPublishSubjectHitungKecepatan: PublishSubject<Boolean> = PublishSubject.create()
    private var mPublishSubjectCekBatasKecepatan: PublishSubject<Boolean> = PublishSubject.create()

    // live data
    private val mGeocoderPenggunaLiveData: MutableLiveData<MsgHasilGeocoder> = MutableLiveData()
    private val mStatesViewLiveData: MutableLiveData<StateData> = MutableLiveData()
    private val mKecepatanItemLiveData: MutableLiveData<KecepatanItems> = MutableLiveData()
    private val mBatasKecepatanStatusLiveData: MutableLiveData<StateKecepatanBatas> =
        MutableLiveData()
    private val mDbSetelanLiveData: MutableLiveData<DbSetelan> = MutableLiveData()


    constructor(application: Application) : super(application) {

        mApplication = application
        mKecepatanParsers =
                KecepatanParsers(mApplication.applicationContext)

        mDataParsers = DataParsers(mApplication.applicationContext)
        mGeocodersLokasi = GeocoderLocationParsers()
    }

    override fun getLiveDataAlamatLokasiPengguna(): LiveData<MsgHasilGeocoder> {

        return mGeocoderPenggunaLiveData
    }

    override fun getLiveDataNilaiKecepatan(): LiveData<KecepatanItems> {

        return mKecepatanItemLiveData
    }

    override fun getLiveDataDbSetelan(): LiveData<DbSetelan> {

        return mDbSetelanLiveData
    }

    override fun getLiveDataStateBatasKecepatan(): LiveData<StateKecepatanBatas> {

        return mBatasKecepatanStatusLiveData
    }

    override fun getLiveDataStateView(): LiveData<StateData> {

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

        mRepoHitungKecepatan.initSubscriptions()
    }

    override fun stopSubscriber() {

        mCompositeDisposable.dispose()
        mRepoHitungKecepatan.stopSubscriptions()
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
                .map {

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

                        val stringAlamatGabungan: String =
                            msghasilgeocoder?.mStringAlamatGabungan ?: ""
                        if (stringAlamatGabungan.isNotEmpty() && msghasilgeocoder != null) {
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

        mCompositeDisposable.add(
            mPublishSubjectHitungKecepatan
                .observeOn(Schedulers.single())
                .map {

                    val kecepatanItems: KecepatanItems = mKecepatanParsers.hitungKecepatanKendaraan(
                        mLocationAwal, mLocationAkhir, mLongWaktuAwal, mLongWaktuAkhir
                    )

                    val stringBatasKecepatan: String = mDbSetelan.stringKecepatanMaks
                    kecepatanItems.mStringBatasKecepatan = stringBatasKecepatan

                    val stringTipeBatasKecepatan: String = mDbSetelan.stringTipeKecepatan
                    kecepatanItems.mStringTipeKecepatanBatas = stringTipeBatasKecepatan

                    // testing error
                    val nilaiKecepatan = (kecepatanItems.mIntKecepatanKmh + 1).toString()
                    return@map kecepatanItems
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { kecepatanitems: KecepatanItems? ->

                        // setel teks kecepatan pengguna
                        if (kecepatanitems != null) {
                            mKecepatanItemsPengguna = kecepatanitems
                            mKecepatanItemLiveData.postValue(mKecepatanItemsPengguna)

                            // task ambil geocoder pengguna
                            startTaskAmbilGeocoderPengguna()

                            // cek batas kecepatan
                            cekBatasKecepatanKendaraan()
                        }
                    },
                    { error: Throwable? ->
                        error?.printStackTrace()
                    }
                )
        )


        mCompositeDisposable.add(
            mPublishSubjectCekBatasKecepatan
                .observeOn(Schedulers.single())
                .map { _: Boolean ->

                    val isKecepatanLewatBatas: Boolean

                    val stringTipeKecepatan: String = mDbSetelan.stringTipeKecepatan
                    val stringBatasKecepatan: String = mDbSetelan.stringKecepatanMaks
                    val doubleBatasKecepatan: Double =
                        stringBatasKecepatan.toDoubleOrNull() ?: 70.toDouble()

                    val mDoubleKecepatanKmh: Double =
                        mKecepatanItemsPengguna.mIntKecepatanKmh.toDouble()
                    val mDoubleKecepatanMph: Double =
                        mKecepatanItemsPengguna.mIntKecepatanMph.toDouble()
                    val mDoubleKecepatanKnot: Double =
                        mKecepatanItemsPengguna.mIntKecepatanKnot.toDouble()

                    when (stringTipeKecepatan) {

                        Konstans.STR_KMH -> {

                            isKecepatanLewatBatas = mDoubleKecepatanKmh >= doubleBatasKecepatan
                        }

                        Konstans.STR_MPH -> {

                            isKecepatanLewatBatas = mDoubleKecepatanMph >= doubleBatasKecepatan
                        }

                        Konstans.STR_KNT -> {

                            isKecepatanLewatBatas = mDoubleKecepatanKnot >= doubleBatasKecepatan
                        }

                        else -> {
                            isKecepatanLewatBatas = mDoubleKecepatanKmh >= doubleBatasKecepatan
                        }
                    }

                    return@map isKecepatanLewatBatas
                }
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { isKecepatanLewatBatas: Boolean? ->

                        if (isKecepatanLewatBatas == true || isKecepatanLewatBatas == false) {
                            sendNotifikasiBatasKecepatan(isKecepatanLewatBatas)
                        }
                    },
                    { error: Throwable? ->
                        error?.printStackTrace()
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

    }

    override fun cekLokasiPengguna(location: Location) {

        mLocationPengguna = location
        mDoubleLatitude = mLocationPengguna.latitude
        mDoubleLongitude = mLocationPengguna.longitude

        val lokasiPenggunaString = mDoubleLatitude.toString() + ", " + mDoubleLongitude.toString()
        val msgHasilGeocoder = MsgHasilGeocoder()

        msgHasilGeocoder.mStringAlamatGabungan = lokasiPenggunaString
        setelTeksLokasiPengguna(msgHasilGeocoder)

        startTaskAmbilGeocoderPengguna()

        // ambil untuk menghitung kecepatan
        mLocationAwal = location
        mLocationAkhir = location

        val dateTimed = DateTime()
        mLongWaktuAwal = dateTimed.millis
        mLongWaktuAkhir = dateTimed.millis

        mDoubleLatitude = mLocationAwal.latitude
        mDoubleLongitude = mLocationAwal.longitude

        isInitPengukurOK = true

        // hitung lokasi kecepatan task
        if (isInitPengukurOK) {
            hitungKecepatanKendaraan()
        }
    }

    override fun cekLokasiPenggunaUpdate(location: Location) {

        mLocationPengguna = location
        mDoubleLatitude = mLocationPengguna.latitude
        mDoubleLongitude = mLocationPengguna.longitude

        val lokasiPenggunaString = mDoubleLatitude.toString() + ", " + mDoubleLongitude.toString()
        val msgHasilGeocoder = MsgHasilGeocoder()
        msgHasilGeocoder.mStringAlamatGabungan = lokasiPenggunaString
        setelTeksLokasiPengguna(msgHasilGeocoder)

        // mulai ambil geocoder
        startTaskAmbilGeocoderPengguna()

        // cek apakah status pengukur sudah di inisialisasi atau belum
        if (!isInitPengukurOK) {

            // ambil untuk menghitung kecepatan
            mLocationAwal = location
            mLocationAkhir = location

            val dateTimed = DateTime()
            mLongWaktuAwal = dateTimed.millis
            mLongWaktuAkhir = dateTimed.millis

            mDoubleLatitude = mLocationAwal.latitude
            mDoubleLongitude = mLocationAwal.longitude

            // setel status pengukur sudah ok
            isInitPengukurOK = true
        } else {
            // jika pengukur sudah diinisialisasi
            //perbarui data waktu, waktu akhir jadi waktu awal,
            // dan waktu baru jadi waktu akhir
            mLongWaktuAwal = mLongWaktuAkhir

            val dateTime = DateTime()
            mLongWaktuAkhir = dateTime.millis

            // perbarui data lokasi, lokasi akhir jadi lokasi awal,
            // lokasi baru jadi lokasi akhir
            mLocationAwal = mLocationAkhir
            mLocationAkhir = location

            mDoubleLatitude = mLocationAkhir.latitude
            mDoubleLongitude = mLocationAkhir.longitude
        }

        //hitung lokasi kecepatan task
        //jika pengukur lagi aktif
        if (isInitPengukurOK) {
            hitungKecepatanKendaraan()
        }
    }

    override fun setelTeksLokasiPengguna(msgHasilGeocoder: MsgHasilGeocoder) {

        mGeocoderPenggunaLiveData.postValue(msgHasilGeocoder)
    }

    override fun startTaskAmbilGeocoderPengguna() {

        if (mPublishSubjectHitungGeocoder.hasObservers()) {
            mPublishSubjectHitungGeocoder.onNext(true)
        }
    }

    override fun hitungKecepatanKendaraan() {

        if (mPublishSubjectHitungKecepatan.hasObservers()) {
            mPublishSubjectHitungKecepatan.onNext(true)
        }
    }


    override fun getDataBatasKecepatanDb() {

        mRepoHitungKecepatan.cekDatabaseSetelan()
    }

    override fun setDataBatasKecepatanDb(dbSetelan: DbSetelan) {

        mDbSetelan = dbSetelan
        mDbSetelanLiveData.postValue(mDbSetelan)
    }


    override fun cekBatasKecepatanKendaraan() {

        if (mPublishSubjectCekBatasKecepatan.hasObservers()) {
            mPublishSubjectCekBatasKecepatan.onNext(true)
        }
    }

    override fun sendNotifikasiBatasKecepatan(isKecepatanLewatBatas: Boolean) {

        val stateKecepatanBatas = StateKecepatanBatas()
        stateKecepatanBatas.intKodeState = StateKonstans.STATE_STATUS_BATAS_KECEPATAN
        stateKecepatanBatas.isBatasMelebihi = isKecepatanLewatBatas
        mBatasKecepatanStatusLiveData.postValue(stateKecepatanBatas)
    }

    override fun stopHitungKecepatan() {

        isInitPengukurOK = false
        restartSubscribers()
    }

    override fun tampilPesanPeringatan(resID: Int) {

        val stateData = StateData()
        stateData.intKodeState = StateKonstans.STATE_SHOW_TOAST
        stateData.intPayloadData = resID
        mStatesViewLiveData.value = stateData
    }
}