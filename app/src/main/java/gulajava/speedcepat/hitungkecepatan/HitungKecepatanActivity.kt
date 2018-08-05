package gulajava.speedcepat.hitungkecepatan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.R
import gulajava.speedcepat.adapters.ViewPagerAdapters
import gulajava.speedcepat.baseactivitys.BaseLocationActivity
import gulajava.speedcepat.baseactivitys.BaseLocationClientContract
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.dialogs.BottomDialogBantuanPengukur
import gulajava.speedcepat.hitungkecepatan.fragmenttabs.FragTabKecepatanKNOT
import gulajava.speedcepat.hitungkecepatan.fragmenttabs.FragTabKecepatanKPH
import gulajava.speedcepat.hitungkecepatan.fragmenttabs.FragTabKecepatanMPH
import gulajava.speedcepat.models.KecepatanItems
import gulajava.speedcepat.models.MsgHasilGeocoder
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKecepatanBatas
import gulajava.speedcepat.states.StateKonstans
import kotlinx.android.synthetic.main.maintab_kecepatanukur.*
import kotlinx.android.synthetic.main.toolbars.*

/**
 * Created by Gulajava Ministudio on 6/23/18.
 */
class HitungKecepatanActivity : BaseLocationActivity(), HitungKecepatanContract.View {

    private var mContext: Context? = null
    private var mToolbar: Toolbar? = null
    private var mActionBar: ActionBar? = null

    private var mBottomNavigationView: BottomNavigationView? = null
    private var mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? =
        null

    private var mViewPager: ViewPager? = null
    private var mViewPagerAdapter: ViewPagerAdapters? = null
    private var mSimpleOnPageChangeListener: ViewPager.SimpleOnPageChangeListener? = null

    private lateinit var mFragTabKecepatanKPH: FragTabKecepatanKPH
    private lateinit var mFragTabKecepatanMPH: FragTabKecepatanMPH
    private lateinit var mFragTabKecepatanKNOT: FragTabKecepatanKNOT

    private var mHandler: Handler? = null
    private var mRunnableJedaInit: Runnable? = null
    private var mRunnableGetDataDb: Runnable? = null

    // view model dan live data
    private var mHitungKecepatanViewModel: HitungKecepatanViewModel? = null
    private var mDbSetelan: DbSetelan = DbSetelan()

    private var mLocationPengguna: Location? = null
    private var mOnLocationUpdatesListenerLokasi: BaseLocationClientContract.OnLocationUpdatesListener? =
        null

    private var mOnClickListenerTombol: View.OnClickListener? = null

    // inisialisasi para data awal
    // ambil data setelan dari database
    // cek permission lokasi
    // ambil lokasi
    // kirim ke fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maintab_kecepatanukur)

        mContext = this@HitungKecepatanActivity

        initViewWidget()

        mToolbar?.let { this@HitungKecepatanActivity.setSupportActionBar(it) }
        mActionBar = this@HitungKecepatanActivity.supportActionBar
        mActionBar?.setTitle(R.string.teks_judul_hitungkecepatan)
        mActionBar?.setSubtitle(R.string.teks_judul_hitungkecepatan_sub)
        mActionBar?.setDisplayHomeAsUpEnabled(true)

        // buat channel id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        initListener()

        initDataAwal()

        initTampilan()

        // inisialisasi untuk lokasi
        initListenerLokasi()
        initDataAwalLokasiTrackKecepatan()
        initViewModelDataLokasi()
        initViewModelObservers()
    }

    override fun onStart() {
        super.onStart()

        mHitungKecepatanViewModel?.initSubscriber()
        mHandler?.postDelayed(mRunnableGetDataDb, 600)
    }

    override fun onStop() {
        super.onStop()
        mHitungKecepatanViewModel?.stopSubscriber()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHitungKecepatanViewModel?.stopSubscriber()
        stopViewModelObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menu?.let {
            this@HitungKecepatanActivity.menuInflater?.inflate(
                R.menu.menu_bantuan_pengukur_kecepatan,
                it
            )
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val idMenu: Int = item?.itemId ?: 0

        when (idMenu) {

            android.R.id.home -> {

                this@HitungKecepatanActivity.finish()
                return true
            }

            R.id.menu_bantuan -> {

                // tampilkan dialog bottom bantuan
                showDialogBantuan()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun initViewWidget() {

        mToolbar = toolbar
        mViewPager = viewpager
        mBottomNavigationView = nav_bottomview
    }

    override fun initDataAwal() {

        mFragTabKecepatanKPH = FragTabKecepatanKPH()
        mFragTabKecepatanMPH = FragTabKecepatanMPH()
        mFragTabKecepatanKNOT = FragTabKecepatanKNOT()

        mHandler = Handler()

        mHitungKecepatanViewModel = ViewModelProviders.of(this@HitungKecepatanActivity)
            .get(HitungKecepatanViewModel::class.java)
    }

    override fun initListener() {

        mOnLocationUpdatesListenerLokasi =
                object : BaseLocationClientContract.OnLocationUpdatesListener {
                    override fun onLocationUpdates(location: Location) {

                        mLocationPengguna = location
                        startHitungKecepatan()
                    }
                }

        mOnLocationUpdatesListenerLokasi?.let {
            setListenerLokasiBase(it)
        }

        mOnNavigationItemSelectedListener =
                BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->

                    val id: Int = item.itemId

                    when (id) {

                        R.id.nav_kecepatankph -> {

                            setSelectedViewPager(0)
                            return@OnNavigationItemSelectedListener true
                        }

                        R.id.nav_kecepatanmph -> {

                            setSelectedViewPager(1)
                            return@OnNavigationItemSelectedListener true
                        }

                        R.id.nav_kecepatanknot -> {

                            setSelectedViewPager(2)
                            return@OnNavigationItemSelectedListener true
                        }
                    }

                    return@OnNavigationItemSelectedListener false
                }

        mSimpleOnPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {

                    0 -> {

                        setSelectedBottomNavigation(R.id.nav_kecepatankph)
                    }

                    1 -> {

                        setSelectedBottomNavigation(R.id.nav_kecepatanmph)
                    }

                    2 -> {

                        setSelectedBottomNavigation(R.id.nav_kecepatanknot)
                    }
                }
            }
        }

        mRunnableJedaInit = Runnable {

            // cek status koneksi internet
            mHitungKecepatanViewModel?.cekStatusKoneksiGPSInternet()

            // cek permission lokasi
            cekPermisiLokasiGPSLacakPosisi()
        }

        mRunnableGetDataDb = Runnable {

            // ambil data setelan dari database
            getDataSetelanBatasKecepatan()
        }

        mOnClickListenerTombol = View.OnClickListener { _: View? ->

        }
    }

    override fun initTampilan() {

        mViewPagerAdapter = this@HitungKecepatanActivity.supportFragmentManager?.let {
            ViewPagerAdapters(it)
        }

        mViewPagerAdapter?.addFragments(mFragTabKecepatanKPH, "KPH")
        mViewPagerAdapter?.addFragments(mFragTabKecepatanMPH, "MPH")
        mViewPagerAdapter?.addFragments(mFragTabKecepatanKNOT, "KNOT")

        mSimpleOnPageChangeListener?.let {
            mViewPager?.addOnPageChangeListener(it)
        }

        mViewPagerAdapter?.let {
            mViewPager?.adapter = it
        }

        mViewPager?.currentItem = 0
        mViewPager?.offscreenPageLimit = 7

        mOnNavigationItemSelectedListener?.let {
            mBottomNavigationView?.setOnNavigationItemSelectedListener(it)
        }
    }

    override fun initViewModelObservers() {

        mHitungKecepatanViewModel?.getLiveDataAlamatLokasiPengguna()
            ?.observe(this@HitungKecepatanActivity,
                Observer { _: MsgHasilGeocoder? ->


                })

        mHitungKecepatanViewModel?.getLiveDataNilaiKecepatan()
            ?.observe(this@HitungKecepatanActivity,
                Observer { _: KecepatanItems? ->

                })

        mHitungKecepatanViewModel?.getLiveDataDbSetelan()?.observe(this@HitungKecepatanActivity,
            Observer { dbsetelan: DbSetelan? ->

                if (dbsetelan != null) {
                    mDbSetelan = dbsetelan
                    mHandler?.postDelayed(mRunnableJedaInit, 1000)
                }
            })

        mHitungKecepatanViewModel?.getLiveDataStateBatasKecepatan()
            ?.observe(this@HitungKecepatanActivity,
                Observer { stateBatasKecepatan: StateKecepatanBatas? ->

                    val idState: Int = stateBatasKecepatan?.intKodeState ?: 0
                    when (idState) {

                        StateKonstans.STATE_STATUS_BATAS_KECEPATAN -> {

                            if (stateBatasKecepatan != null) {
                                val isBatasMelebihi: Boolean = stateBatasKecepatan.isBatasMelebihi
                                if (isBatasMelebihi) {
                                    showStatusBatasKecepatan()
                                } else {
                                    sembunyikanStatusBatasKecepatan()
                                }
                            }
                        }
                    }
                })

        mHitungKecepatanViewModel?.getLiveDataStateView()?.observe(this@HitungKecepatanActivity,
            Observer { statedata: StateData? ->

                if (statedata != null) {
                    checkStateLiveData(statedata)
                }
            })

    }

    override fun stopViewModelObservers() {

        mHitungKecepatanViewModel?.getLiveDataAlamatLokasiPengguna()
            ?.removeObservers(this@HitungKecepatanActivity)
        mHitungKecepatanViewModel?.getLiveDataNilaiKecepatan()
            ?.removeObservers(this@HitungKecepatanActivity)
        mHitungKecepatanViewModel?.getLiveDataDbSetelan()
            ?.removeObservers(this@HitungKecepatanActivity)
        mHitungKecepatanViewModel?.getLiveDataStateBatasKecepatan()
            ?.removeObservers(this@HitungKecepatanActivity)
        mHitungKecepatanViewModel?.getLiveDataStateView()
            ?.removeObservers(this@HitungKecepatanActivity)
    }

    override fun checkStateLiveData(stateData: StateData) {

        val idState: Int = stateData.intKodeState

        when (idState) {

            StateKonstans.STATE_SHOW_TOAST -> {

                tampilPesanPeringatan(stateData.intPayloadData)
            }
        }
    }

    override fun setSelectedBottomNavigation(intIdBottomNav: Int) {

        mBottomNavigationView?.selectedItemId = intIdBottomNav
    }

    override fun setSelectedViewPager(intPosisi: Int) {

        mViewPager?.currentItem = intPosisi
    }

    override fun getDataSetelanBatasKecepatan() {

        mHitungKecepatanViewModel?.getDataBatasKecepatanDb()
    }

    override fun cekPermisiLokasiGPSLacakPosisi() {

        cekPermissionLokasiGPSTrack()
    }

    override fun startHitungKecepatan() {

        mLocationPengguna?.let {
            mHitungKecepatanViewModel?.cekLokasiPenggunaUpdate(it)
        }
    }

    override fun showStatusBatasKecepatan() {

        // buat channel id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val intentNotifikasi = Intent()
        val intentNotifikasiPendingIntent = PendingIntent.getActivity(
            this@HitungKecepatanActivity, 0, intentNotifikasi, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stringBatasKecepatan: String =
            mContext?.resources?.getString(R.string.teks_notif_judul_pesan_batas_kecepatan) ?: ""
        val stringBatasKecepatanIsi: String =
            mContext?.resources?.getString(R.string.teks_notif_pesan_bataskecepatan) ?: ""

        val notifBuilder =
            NotificationCompat.Builder(
                this@HitungKecepatanActivity,
                Konstans.ID_CHANNEL_NOTIFICATION_KECEPATAN
            )
                .setContentTitle(stringBatasKecepatan)
                .setContentText(stringBatasKecepatanIsi)
                .setSmallIcon(R.drawable.ic_stat_batas_kendaraan_tercapai)
                .setColor(
                    ContextCompat.getColor(
                        this@HitungKecepatanActivity,
                        R.color.colorPrimary
                    )
                )
                .setContentIntent(intentNotifikasiPendingIntent).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX).setOngoing(false)

        val teksbesarStyle = NotificationCompat.InboxStyle()
        teksbesarStyle.setBigContentTitle(stringBatasKecepatan)
        teksbesarStyle.addLine("" + stringBatasKecepatanIsi)
        notifBuilder.setStyle(teksbesarStyle)

        val notificationManagerCompat = NotificationManagerCompat.from(this@HitungKecepatanActivity)
        notificationManagerCompat.notify(
            Konstans.ID_NOTIFIKASI_BATAS_KECEPATAN,
            notifBuilder.build()
        )
    }

    override fun sembunyikanStatusBatasKecepatan() {

        try {
            val notificationManagerCompat: NotificationManagerCompat? =
                NotificationManagerCompat.from(this@HitungKecepatanActivity)
            notificationManagerCompat?.cancel(Konstans.ID_NOTIFIKASI_BATAS_KECEPATAN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showDialogBantuan() {

        val fragmentManager: FragmentManager? = this@HitungKecepatanActivity.supportFragmentManager
        val dialogBantuanPengukur = BottomDialogBantuanPengukur()
        fragmentManager?.let {
            dialogBantuanPengukur.show(it, "dialog_bantuan_pengukur")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannel(): String {

        val channelId = Konstans.ID_CHANNEL_NOTIFICATION_KECEPATAN
        try {
            val channelName = Konstans.ID_NAME_CHANNEL_NOTIFICATION_KECEPATAN
            val notificationChannel = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.lightColor =
                    ContextCompat.getColor(this@HitungKecepatanActivity, R.color.colorAccent)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val serviceNotification: NotificationManager? =
                getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            serviceNotification?.createNotificationChannel(notificationChannel)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return channelId
    }

    override fun tampilPesanPeringatan(resID: Int) {

        mToolbar?.let { toolbar ->

            mContext?.let { context ->

                mOnClickListenerTombol?.let { onClickListener ->

                    Snackbar.make(toolbar, resID, Snackbar.LENGTH_SHORT)
                        .setAction("OK", onClickListener)
                        .setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
            }
        }
    }
}