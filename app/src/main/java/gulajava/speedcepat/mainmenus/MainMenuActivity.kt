package gulajava.speedcepat.mainmenus

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import gulajava.speedcepat.R
import gulajava.speedcepat.baseactivitys.BaseLocationActivity
import gulajava.speedcepat.baseactivitys.BaseLocationClientContract
import gulajava.speedcepat.hitungkecepatan.HitungKecepatanActivity
import gulajava.speedcepat.models.MsgHasilGeocoder
import gulajava.speedcepat.models.mainmenus.StatusGPSInternetData
import gulajava.speedcepat.setelan.SetelanAplikasiActivity
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKonstans
import kotlinx.android.synthetic.main.incl_mainmenu_settingcard.*
import kotlinx.android.synthetic.main.mainmenu.*
import kotlinx.android.synthetic.main.toolbars.*

/**
 * Created by Gulajava Ministudio on 6/4/18.
 */
class MainMenuActivity : BaseLocationActivity(), MainMenuContract.View {

    private var mContext: Context? = null

    // widget tampilan
    private var mToolbar: Toolbar? = null
    private var mActionBar: ActionBar? = null

    private var mTextViewStatusGPS: TextView? = null
    private var mTextViewSambunganInternet: TextView? = null
    private var mTextViewLokasiPengguna: TextView? = null

    private var mButtonSetelanPerangkat: Button? = null
    private var mButtonUkurKecepatan: Button? = null
    private var mButtonSetelanAplikasi: Button? = null

    private var mOnClickListenerTombol: View.OnClickListener? = null

    // view model main menu
    private var mMainMenuViewModel: MainMenuViewModel? = null

    private var mHandler: Handler? = null
    private var mRunnableJedaInit: Runnable? = null
    private var mIntentPindahHalaman: Intent? = null

    private var mOnLocationUpdatesListenerLokasi: BaseLocationClientContract.OnLocationUpdatesListener? =
        null

    private var mLocationPengguna: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainmenu)
        mContext = this@MainMenuActivity

        initViewWidget()

        mToolbar?.let { this@MainMenuActivity.setSupportActionBar(it) }
        mActionBar = this@MainMenuActivity.supportActionBar
        mActionBar?.setTitle(R.string.app_name)
        mActionBar?.setSubtitle(R.string.app_name_sub)

        initViewModel()

        initListener()

        initDataAwal()

        initTampilan()

        // inisialisasi layanan pengambilan lokasi gps
        initListenerLokasi()

        initDataAwalLokasi()

        initViewModelDataLokasi()

        initViewModelObservers()
    }

    override fun onStart() {
        super.onStart()

        mMainMenuViewModel?.initSubscriber()
        mHandler?.postDelayed(mRunnableJedaInit, 1000)
    }

    override fun onStop() {
        super.onStop()
        mMainMenuViewModel?.stopSubscriber()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopViewModelObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            this@MainMenuActivity.menuInflater?.inflate(R.menu.menu_main, it)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        val idMenu: Int = item?.itemId ?: 0

        when (idMenu) {

            R.id.menu_bantuan -> {

                return true
            }

            R.id.menu_tentang_app -> {

                return true
            }
        }

        return false
    }

    override fun initViewWidget() {

        mToolbar = toolbar
        mTextViewStatusGPS = teks_status_kondisi_gps
        mTextViewSambunganInternet = teks_status_kondisi_internet

        mTextViewLokasiPengguna = teks_status_kondisi_lokasipengguna

        mButtonSetelanPerangkat = tombol_setelan_perangkat
        mButtonUkurKecepatan = tombol_ukur_kecepatan
        mButtonSetelanAplikasi = tombol_setelan_aplikasi
    }

    override fun initDataAwal() {

        mHandler = Handler()

    }

    override fun initListener() {

        mOnClickListenerTombol = View.OnClickListener { view: View? ->

            val idView: Int = view?.id ?: 0

            when (idView) {

                R.id.tombol_setelan_perangkat -> {

                    //ke setelan lokasi
                    try {
                        mIntentPindahHalaman = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        mIntentPindahHalaman?.let { this@MainMenuActivity.startActivity(it) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                R.id.tombol_ukur_kecepatan -> {

                    mMainMenuViewModel?.cekStatusKoneksiGPSInternetPindahHalaman()
                }

                R.id.tombol_setelan_aplikasi -> {

                    mIntentPindahHalaman =
                            mContext?.let { Intent(it, SetelanAplikasiActivity::class.java) }
                    mIntentPindahHalaman?.let { this@MainMenuActivity.startActivity(it) }
                }
            }
        }

        mOnLocationUpdatesListenerLokasi =
                object : BaseLocationClientContract.OnLocationUpdatesListener {
                    override fun onLocationUpdates(location: Location) {

                        mLocationPengguna = location
                        ambilLokasiPenggunaDariBus()
                    }
                }

        mOnLocationUpdatesListenerLokasi?.let {
            setListenerLokasiBase(it)
        }

        mRunnableJedaInit = Runnable {

            // cek permission lokasi
            cekPermisiLokasiGPSLacakPosisi()

            // cek status koneksi internet
            cekKoneksiInternetGPS()
        }
    }

    override fun initTampilan() {

        mOnClickListenerTombol?.let { onClickListener ->

            mButtonUkurKecepatan?.setOnClickListener(onClickListener)
            mButtonSetelanAplikasi?.setOnClickListener(onClickListener)
            mButtonSetelanPerangkat?.setOnClickListener(onClickListener)
        }
    }

    override fun initViewModel() {

        mMainMenuViewModel =
                ViewModelProviders.of(this@MainMenuActivity).get(MainMenuViewModel::class.java)
    }

    override fun initViewModelObservers() {

        if (mMainMenuViewModel?.getAlamatPenggunaLiveData()?.hasObservers() == false) {
            mMainMenuViewModel?.getAlamatPenggunaLiveData()
                ?.observe(this@MainMenuActivity, Observer { msgGeocoder: MsgHasilGeocoder? ->

                    if (msgGeocoder != null) {
                        setelTeksLokasiPengguna(msgGeocoder.mStringAlamatGabungan)
                    }
                })
        }

        if (mMainMenuViewModel?.getStatusGPSInternetData()?.hasObservers() == false) {
            mMainMenuViewModel?.getStatusGPSInternetData()
                ?.observe(
                    this@MainMenuActivity,
                    Observer { statusGpsInternetData: StatusGPSInternetData? ->

                        if (statusGpsInternetData != null) {
                            setStatusGPSKoneksiInternet(
                                statusGpsInternetData.stringStatusGPS,
                                statusGpsInternetData.stringStatusKoneksiInternet
                            )
                        }
                    })
        }

        if (mMainMenuViewModel?.getStateViewLiveData()?.hasObservers() == false) {
            mMainMenuViewModel?.getStateViewLiveData()
                ?.observe(this@MainMenuActivity, Observer { stateData: StateData? ->

                    if (stateData != null) {
                        checkStateLiveData(stateData)
                    }
                })
        }
    }

    override fun stopViewModelObservers() {

        mMainMenuViewModel?.getAlamatPenggunaLiveData()?.removeObservers(this@MainMenuActivity)
        mMainMenuViewModel?.getStatusGPSInternetData()?.removeObservers(this@MainMenuActivity)
        mMainMenuViewModel?.getStateViewLiveData()?.removeObservers(this@MainMenuActivity)
    }

    override fun checkStateLiveData(stateData: StateData) {

        val kodeState = stateData.intKodeState

        when (kodeState) {

            StateKonstans.STATE_SHOW_TOAST -> {

                val kodeResID = stateData.intPayloadData
                showToastPeringatan(kodeResID)
            }

            StateKonstans.STATE_PINDAH_HALAMAN_UKURKECEPATAN -> {

                // pindah halaman pengukur kecepatan
                val intent: Intent? =
                    mContext?.let { Intent(it, HitungKecepatanActivity::class.java) }
                intent?.let { this@MainMenuActivity.startActivity(it) }
            }
        }
    }

    override fun cekPermisiLokasiGPSLacakPosisi() {

        cekPermissionLokasiGPSTrack()
    }

    override fun ambilLokasiPenggunaDariBus() {

        mLocationPengguna?.let {
            mMainMenuViewModel?.cekLokasiPengguna(it)
        }
    }

    override fun cekKoneksiInternetGPS() {

        mMainMenuViewModel?.cekStatusKoneksiGPSInternet()
    }

    override fun setStatusGPSKoneksiInternet(
        stringStatusGPS: String,
        stringStatusInternetKoneksi: String
    ) {

        mTextViewStatusGPS?.text = stringStatusGPS
        mTextViewSambunganInternet?.text = stringStatusInternetKoneksi
    }

    override fun startHitungKecepatan() {


    }

    override fun setelTeksLokasiPengguna(lokasiPengguna: String) {

        mTextViewLokasiPengguna?.text = lokasiPengguna
    }

    override fun tampilPesanPeringatan(resID: Int) {

        mContext?.let {
            Toast.makeText(it, resID, Toast.LENGTH_SHORT).show()
        }
    }
}