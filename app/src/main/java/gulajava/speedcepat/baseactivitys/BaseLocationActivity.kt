package gulajava.speedcepat.baseactivitys

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import gulajava.jdwl.locations.CekGPSNet
import gulajava.jdwl.locations.GoogleLocationAPIClient
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.R
import gulajava.speedcepat.dialogs.DialogGagalGpsLokasi
import gulajava.speedcepat.models.modelbus.MessageBusAktAkt
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by Gulajava Ministudio on 5/27/18.
 */
open class BaseLocationActivity : AppCompatActivity(), BaseLocationClientContract,
    EasyPermissions.PermissionCallbacks {

    // GPS DAN LOKASI
    var isInternet = false
    var isNetworkNyala = false
    var isGPSNyala = false
    var isNetworkGPS = false
    private var cekGpsNet: CekGPSNet? = null
    var mLocationSaya: Location? = null

    // UNTUK PENGAMBILAN LOKASI
    private var isPermissionLokasiOK = false
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var mCompositeDisposable: CompositeDisposable

    // setelan lokasi
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    // untuk location tipe last known
    private var mLocationRequestGPSTrack: LocationRequest? = null

    // callback dari listener
    private var mLocationCallback: LocationCallback? = null
    // callback untuk listener lokasi antar kelas activity
    var mOnLocationUpdatesListener: BaseLocationClientContract.OnLocationUpdatesListener? = null

    // live data arch component
    private var mBaseLocationViewModel: BaseLocationViewModel? = null

    // alur aktifkan gps :
    // cek permission lokasi dahulu
    // lanjutkan dengan cek setelan gps perangkat
    // kemudian cek status gps lagi
    // aktifkan metode ambil lokasi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        } else {
            if (this::mCompositeDisposable.isInitialized && mCompositeDisposable.isDisposed) {
                mCompositeDisposable = CompositeDisposable()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        } else {
            if (this::mCompositeDisposable.isInitialized && mCompositeDisposable.isDisposed) {
                mCompositeDisposable = CompositeDisposable()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mCompositeDisposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGPSTrackLokasi()
    }

    override fun getLocation(): Location? {

        return mLocationSaya
    }

    override fun setLokasi(location: Location) {

        cekStatusInternet()
        mLocationSaya = location

        mLocationSaya?.let {
            mOnLocationUpdatesListener?.onLocationUpdates(it)
        }
    }

    override fun initDataAwalLokasi() {

        mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this@BaseLocationActivity)
        mSettingsClient = LocationServices.getSettingsClient(this@BaseLocationActivity)
        mLocationRequestGPSTrack = GoogleLocationAPIClient.getLocationGPSReq()

        mLocationRequestGPSTrack?.let {

            val locationsettingReqBuilder =
                LocationSettingsRequest.Builder().addLocationRequest(it).setAlwaysShow(true)
            mLocationSettingsRequest = locationsettingReqBuilder.build()
        }
    }

    override fun initDataAwalLokasiTrackKecepatan() {

        mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this@BaseLocationActivity)
        mSettingsClient = LocationServices.getSettingsClient(this@BaseLocationActivity)
        mLocationRequestGPSTrack = GoogleLocationAPIClient.getLocationGPSReqKecepatanAkurasi()

        mLocationRequestGPSTrack?.let {

            val locationsettingReqBuilder =
                LocationSettingsRequest.Builder().addLocationRequest(it).setAlwaysShow(true)
            mLocationSettingsRequest = locationsettingReqBuilder.build()
        }
    }

    override fun initListenerLokasi() {

        mLocationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                if (locationResult != null) {
                    val location: Location? = locationResult.lastLocation
                    if (location != null && isPermissionLokasiOK) {
                        setLokasi(location)
                    }
                }
            }
        }
    }

    override fun initViewModelDataLokasi() {

        mBaseLocationViewModel = ViewModelProviders.of(this@BaseLocationActivity)
            .get(BaseLocationViewModel::class.java)

        mFusedLocationProviderClient?.let { fusedLocationProviderClient ->
            mLocationRequestGPSTrack?.let { locationRequest ->
                mBaseLocationViewModel?.initLiveData(fusedLocationProviderClient, locationRequest)
            }
        }
    }

    override fun cekPermissionLokasiGPSOnly() {

        if (EasyPermissions.hasPermissions(
                this@BaseLocationActivity, *Konstans.LOCATION_PERMISSIONS
            )) {

            // Location permission has been granted, continue as usual.
            isPermissionLokasiOK = true

            // kirim pesan bus lokasi jalan
            kirimPesanBusLokasiJalan(Konstans.KODE_IJINLOKASIOK)
        } else {
            isPermissionLokasiOK = false
            EasyPermissions.requestPermissions(
                this@BaseLocationActivity,
                this@BaseLocationActivity.resources.getString(R.string.teks_isimintalokasi),
                Konstans.REQUEST_CODE_PERMISILOKASI,
                *Konstans.LOCATION_PERMISSIONS
            )
        }
    }

    override fun cekPermissionLokasiGPSTrack() {

        if (EasyPermissions.hasPermissions(
                this@BaseLocationActivity, *Konstans.LOCATION_PERMISSIONS
            )) {

            // Location permission has been granted, continue as usual.
            isPermissionLokasiOK = true

            // sambungkan ke google api client
            // cek setelan gps perangkat
            cekSetelanGPSPerangkat()
        } else {

            isPermissionLokasiOK = false
            EasyPermissions.requestPermissions(
                this@BaseLocationActivity,
                this@BaseLocationActivity.resources.getString(R.string.teks_isimintalokasi),
                Konstans.REQUEST_CODE_LOCATION_TRACK,
                *Konstans.LOCATION_PERMISSIONS
            )
        }
    }

    override fun cekSetelanGPSPerangkat() {

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient?.checkLocationSettings(mLocationSettingsRequest)
            ?.addOnSuccessListener { _: LocationSettingsResponse? ->

                // cek status gps perangkat
                cekStatusGPSAktif()
            }?.addOnFailureListener { exception: java.lang.Exception ->

                try {
                    exception.printStackTrace()
                    val statusCode = (exception as ApiException).statusCode

                    when (statusCode) {

//                        CommonStatusCodes.RESOLUTION_REQUIRED -> {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = exception as ResolvableApiException
                                rae.startResolutionForResult(
                                    this@BaseLocationActivity, Konstans.REQUEST_CHECK_SETTINGS
                                )
                            } catch (sie: Exception) {
                                sie.printStackTrace()
                            }
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                            Toast.makeText(
                                this@BaseLocationActivity,
                                R.string.toast_setelanlokasi_gagal,
                                Toast.LENGTH_LONG
                            ).show()
                            tampilDialogLokasiGagal()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    override fun cekStatusGPSAktif() {

        cekGpsNet = CekGPSNet(this@BaseLocationActivity)

        isInternet = cekGpsNet?.cekStatusInternet() ?: false
        isNetworkNyala = cekGpsNet?.cekStatusNetworkGSM() ?: false

        //provider gps dan provider network untuk lokasi
        isGPSNyala = cekGpsNet?.cekStatusGPS() ?: false
        isNetworkGPS = cekGpsNet?.cekStatusNetwork() ?: false

        if (isInternet && isNetworkGPS || isNetworkNyala && isNetworkGPS) {

            // cek posisi terakhir pengguna
            // jalankan proses update lokasi
            cekLokasiTerakhir()
        }

        //internet ga jalan, gps ga jalan, dan jaringan seluler juga mati
        if (!isInternet && !isNetworkNyala || !isNetworkGPS) {
            //tampilkan dialog jaringan dan gps ga nyala
            tampilDialogLokasiGagal()
        }
    }

    override fun cekStatusInternet() {

        val cekGpsNet = CekGPSNet(this@BaseLocationActivity)
        isInternet = cekGpsNet.cekStatusInternet()
    }

    override fun cekLokasiTerakhir() {

        // cek dulu sudah di set observer atau belum
        if (mBaseLocationViewModel?.getLastLocationLiveData()?.hasObservers() == false) {

            mBaseLocationViewModel?.getLastLocationLiveData()
                ?.observe(this@BaseLocationActivity, Observer { location: Location? ->

                    if (location != null) {
                        setLokasi(location)
                    }
                })
        }

        // jalankan track GPS
        aktifkanGPSTrackLokasi()
    }

    override fun aktifkanGPSTrackLokasi() {

        if (mBaseLocationViewModel?.getLocationTrackLiveData()?.hasObservers() == false) {

            mBaseLocationViewModel?.getLocationTrackLiveData()?.observe(this@BaseLocationActivity,
                Observer { location: Location? ->

                    if (location != null && isPermissionLokasiOK) {
                        setLokasi(location)
                    }
                })
        }
    }

    override fun stopGPSTrackLokasi() {

        mBaseLocationViewModel?.getLastLocationLiveData()
            ?.removeObservers(this@BaseLocationActivity)
        mBaseLocationViewModel?.getLocationTrackLiveData()
            ?.removeObservers(this@BaseLocationActivity)
    }

    override fun tampilDialogLokasiGagal() {

        val singleDialog: Single<Boolean> = Single.fromCallable {

            val dialogGagalGpsLokasi = DialogGagalGpsLokasi()
            dialogGagalGpsLokasi.isCancelable = false

            val fragmentManager = this@BaseLocationActivity.supportFragmentManager
            fragmentManager?.let { dialogGagalGpsLokasi.show(it, "dialog_gagal_lokasi") }
            return@fromCallable true
        }

        mCompositeDisposable.add(
            singleDialog.observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(t: Boolean) {

                    }

                    override fun onError(e: Throwable) {

                        e.printStackTrace()
                    }

                })
        )
    }

    override fun kirimPesanBusLokasiJalan(kodePesan: Int) {

        val messageBusAktAkt = MessageBusAktAkt()
        messageBusAktAkt.mIntKode = kodePesan
        messageBusAktAkt.mStringPesanTambahan = ""
        EventBus.getDefault().post(messageBusAktAkt)
    }

    override fun showToastPeringatan(resID: Int) {

        try {
            Toast.makeText(this@BaseLocationActivity, resID, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setListenerLokasiBase(onLocationListener: BaseLocationClientContract.OnLocationUpdatesListener) {

        mOnLocationUpdatesListener = onLocationListener
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        isPermissionLokasiOK = false

        showToastPeringatan(R.string.teks_isimintalokasi)

        try {
            val listPerms: ArrayList<String> = perms as? ArrayList<String> ?: ArrayList()
            if (requestCode == Konstans.REQUEST_CODE_PERMISILOKASI && EasyPermissions.somePermissionPermanentlyDenied(
                    this@BaseLocationActivity, listPerms
                )) {

                val builder = AppSettingsDialog.Builder(this@BaseLocationActivity)
                builder.setTitle(this@BaseLocationActivity.resources.getString(R.string.toast_permisi_setelan_perangkat))
                builder.setPositiveButton(this@BaseLocationActivity.resources.getString(R.string.teks_setelan_perangkat))
                builder.setNegativeButton(this@BaseLocationActivity.resources.getString(R.string.teks_batal))
                builder.setRationale(R.string.teks_isimintalokasi)
                builder.setRequestCode(Konstans.REQUEST_CODE_PERMISILOKASI)

                val appSettingsDialog = builder.build()
                appSettingsDialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {

            val listPermsRejected: ArrayList<String> = perms as? ArrayList<String> ?: ArrayList()

            if (requestCode == Konstans.REQUEST_CODE_LOCATION_TRACK && EasyPermissions.somePermissionPermanentlyDenied(
                    this@BaseLocationActivity, listPermsRejected
                )) {

                val builder = AppSettingsDialog.Builder(this@BaseLocationActivity)
                builder.setTitle(this@BaseLocationActivity.resources.getString(R.string.toast_permisi_setelan_perangkat))
                builder.setRationale(R.string.teks_isimintalokasi)
                builder.setPositiveButton(this@BaseLocationActivity.resources.getString(R.string.teks_setelan_perangkat))
                builder.setNegativeButton(this@BaseLocationActivity.resources.getString(R.string.teks_batal))
                builder.setRequestCode(Konstans.REQUEST_CODE_LOCATION_TRACK)

                val appSettingsDialog = builder.build()
                appSettingsDialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // minta lagi permission lokasi jika masih ditolak
        if (requestCode == Konstans.REQUEST_CODE_LOCATION_TRACK || requestCode == Konstans.REQUEST_CODE_PERMISILOKASI) {

            val builder = AppSettingsDialog.Builder(this@BaseLocationActivity)
            builder.setTitle(this@BaseLocationActivity.resources.getString(R.string.toast_permisi_setelan_perangkat))
            builder.setRationale(R.string.teks_isimintalokasi)
            builder.setPositiveButton(this@BaseLocationActivity.resources.getString(R.string.teks_setelan_perangkat))
            builder.setNegativeButton(this@BaseLocationActivity.resources.getString(R.string.teks_batal))
            builder.setRequestCode(Konstans.REQUEST_CODE_LOCATION_TRACK)

            val appSettingsDialog = builder.build()
            appSettingsDialog.show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        isPermissionLokasiOK = true

        if (requestCode == Konstans.REQUEST_CODE_LOCATION_TRACK) {
            cekPermissionLokasiGPSTrack()
        }

        if (requestCode == Konstans.REQUEST_CODE_PERMISILOKASI) {
            cekPermissionLokasiGPSOnly()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}