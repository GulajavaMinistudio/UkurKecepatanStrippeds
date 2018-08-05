package gulajava.speedcepat.hitungkecepatan.fragmenttabs

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.R
import gulajava.speedcepat.hitungkecepatan.HitungKecepatanViewModel
import gulajava.speedcepat.models.BarKecepatanTampilItems
import gulajava.speedcepat.models.KecepatanItems
import gulajava.speedcepat.models.MsgHasilGeocoder
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKecepatanBatas
import gulajava.speedcepat.states.StateKonstans
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.frag_kecepatan_ukur.view.*

/**
 * Created by Gulajava Ministudio on 6/24/18.
 */
class FragTabKecepatanKNOT : Fragment(), FragTabKecepatanContract.View {

    private var mContext: Context? = null
    private var mView: View? = null

    private lateinit var mCompositeDisposable: CompositeDisposable

    private var mTextViewNilaiKecepatan: TextView? = null
    private var mTextViewSatuanKecepatan: TextView? = null
    private var mTextViewStatusBatasKecepatan: TextView? = null
    private var mTextViewLokasiPengguna: TextView? = null

    // arch component
    private var mHitungKecepatanViewModel: HitungKecepatanViewModel? = null
    private var mFragTabKecepatanViewModel: FragTabKecepatanViewModel? = null

    // ukuran bar kecepatan
    private var mLinearLayoutBarKecepatan: LinearLayout? = null
    private var mLinearLayoutBarKecepatanBackground: LinearLayout? = null

    private var mLayoutParamsSpeed: LinearLayout.LayoutParams? = null
    private var mLayoutParamsSpeedBackground: LinearLayout.LayoutParams? = null
    private var mIntBarTampil: Double = 0.0
    private var mIntBarTampilBg: Double = 0.0

    private var mActivityParent: FragmentActivity? = null

    private var mKecepatanItems: KecepatanItems = KecepatanItems()
    private var mBarKecepatanTampilItems: BarKecepatanTampilItems = BarKecepatanTampilItems()
    private var msgHasilGeocoder: MsgHasilGeocoder = MsgHasilGeocoder()
    private var isKecepatanMelebihiBatas: Boolean = true
    private var mStateKecepatanBatas: StateKecepatanBatas = StateKecepatanBatas()

    private var mStateViewFragment: StateData = StateData()

    private val mPublishSubjectKecepatan: PublishSubject<BarKecepatanTampilItems> =
        PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view: View? = inflater.inflate(R.layout.frag_kecepatan_ukur, container, false)
        mContext = this@FragTabKecepatanKNOT.context
        mActivityParent = this@FragTabKecepatanKNOT.activity

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view

        initViewWidget()

        initListener()

        initDataAwal()

        initTampilan()

        initViewModelObservers()
    }

    override fun onStart() {
        super.onStart()
        initSubscriber()
        mFragTabKecepatanViewModel?.initSubscriber()
    }

    override fun onStop() {
        super.onStop()
        stopSubscriber()
        mFragTabKecepatanViewModel?.stopSubscriber()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopViewModelObservers()
    }

    override fun initDataAwal() {

        mFragTabKecepatanViewModel = ViewModelProviders.of(this@FragTabKecepatanKNOT)
            .get(FragTabKecepatanViewModel::class.java)

        mActivityParent?.let {
            mHitungKecepatanViewModel =
                    ViewModelProviders.of(it).get(HitungKecepatanViewModel::class.java)
        }

        // setel tipe fragment tempat viewmodel dipakai
        mFragTabKecepatanViewModel?.setTipeBatasKecepatanFragment(Konstans.STR_KNT)
    }

    override fun initListener() {


    }

    override fun initViewWidget() {

        mTextViewNilaiKecepatan = mView?.teks_kecepatan
        mTextViewSatuanKecepatan = mView?.teksatuan
        mTextViewStatusBatasKecepatan = mView?.teksstatuscepatan
        mTextViewLokasiPengguna = mView?.teks_posisipenggunasekarang

        mLinearLayoutBarKecepatan = mView?.barkecepatan
        mLinearLayoutBarKecepatanBackground = mView?.barkecepatanbg
    }

    override fun initTampilan() {

        mTextViewNilaiKecepatan?.text = "0"
        mTextViewSatuanKecepatan?.text = Konstans.STR_KNT
        mTextViewStatusBatasKecepatan?.setText(R.string.teks_batas_kecepatan_aman)

        mContext?.let {
            mTextViewStatusBatasKecepatan?.setTextColor(
                ContextCompat.getColor(
                    it,
                    R.color.hijaunow
                )
            )
        }

        mTextViewLokasiPengguna?.setText(R.string.kartustat_lokasipengguna_belumtersedia)
    }

    override fun initViewModelObservers() {

        // dari activity parent untuk tab fragment
        mActivityParent?.let { fragmentActivity ->

            mHitungKecepatanViewModel?.getLiveDataNilaiKecepatan()?.observe(fragmentActivity,
                Observer { kecepatanitems: KecepatanItems? ->

                    if (kecepatanitems != null) {
                        mKecepatanItems = kecepatanitems
                        hitungBarTampilanKecepatan()
                    }
                })

            mHitungKecepatanViewModel?.getLiveDataAlamatLokasiPengguna()?.observe(fragmentActivity,
                Observer { msghasilgeocoder: MsgHasilGeocoder? ->

                    if (msghasilgeocoder != null) {
                        msgHasilGeocoder = msghasilgeocoder
                        setTeksLokasiPengguna()
                    }
                })

            mHitungKecepatanViewModel?.getLiveDataStateBatasKecepatan()?.observe(fragmentActivity,
                Observer { statekecepatanbatas: StateKecepatanBatas? ->

                    if (statekecepatanbatas != null) {
                        mStateKecepatanBatas = statekecepatanbatas
                        setStatusBatasKecepatan()
                    }
                })
        }


        // untuk fragment viewmodel
        mFragTabKecepatanViewModel?.getLiveDataBarKecepatanItem()?.observe(this@FragTabKecepatanKNOT,
            Observer { bartampilitems: BarKecepatanTampilItems? ->

                Log.w("KECEPATAN KNOT","KNOT")
                if (bartampilitems != null) {
                    mBarKecepatanTampilItems = bartampilitems
                    setTampilanKecepatan()
                }
            })

        mFragTabKecepatanViewModel?.getLiveDataStateView()?.observe(this@FragTabKecepatanKNOT,
            Observer { statedatafragment: StateData? ->

                if (statedatafragment != null) {
                    mStateViewFragment = statedatafragment
                    val idState = mStateViewFragment.intKodeState

                    when (idState) {

                        StateKonstans.STATE_SHOW_TOAST -> {

                            val idRes: Int = mStateViewFragment.intPayloadData
                            showToast(idRes)
                        }
                    }
                }
            })
    }

    override fun stopViewModelObservers() {

        mActivityParent?.let {
            mHitungKecepatanViewModel?.getLiveDataNilaiKecepatan()?.removeObservers(it)
            mHitungKecepatanViewModel?.getLiveDataAlamatLokasiPengguna()?.removeObservers(it)
            mHitungKecepatanViewModel?.getLiveDataStateBatasKecepatan()?.removeObservers(it)
        }

        mFragTabKecepatanViewModel?.getLiveDataBarKecepatanItem()
            ?.removeObservers(this@FragTabKecepatanKNOT)
        mFragTabKecepatanViewModel?.getLiveDataStateView()
            ?.removeObservers(this@FragTabKecepatanKNOT)
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

    override fun restartSubscriber() {

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
            mPublishSubjectKecepatan
                .observeOn(Schedulers.single())
                .map { barkecepatanitems: BarKecepatanTampilItems ->

                    mIntBarTampil = barkecepatanitems.mDoubleBarTampil
                    mIntBarTampilBg = barkecepatanitems.mDoubleBarTampilBg

                    mLayoutParamsSpeed =
                            mLinearLayoutBarKecepatan?.layoutParams as? LinearLayout.LayoutParams
                    mLayoutParamsSpeedBackground =
                            mLinearLayoutBarKecepatanBackground?.layoutParams as? LinearLayout.LayoutParams
                    mLayoutParamsSpeed?.weight = mIntBarTampil.toFloat()
                    mLayoutParamsSpeedBackground?.weight = mIntBarTampilBg.toFloat()

                    return@map true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map { _: Boolean ->

                    mLinearLayoutBarKecepatan?.layoutParams = mLayoutParamsSpeed
                    mLinearLayoutBarKecepatanBackground?.layoutParams = mLayoutParamsSpeedBackground

                    val stringKecepatanNilai: String = mKecepatanItems.mIntKecepatanKnot.toString()
                    mTextViewNilaiKecepatan?.text = stringKecepatanNilai
                    return@map true
                }
                .subscribe(
                    { _: Boolean? ->

                    },
                    { error: Throwable? ->

                        error?.printStackTrace()
                        restartSubscriber()
                    }
                )
        )
    }

    override fun hitungBarTampilanKecepatan() {

        mFragTabKecepatanViewModel?.hitungBatasKecepatanBarTampil(mKecepatanItems)
    }

    override fun setTampilanKecepatan() {

        if (mPublishSubjectKecepatan.hasObservers()) {
            mPublishSubjectKecepatan.onNext(mBarKecepatanTampilItems)
        }
    }

    override fun setTeksLokasiPengguna() {

        val stringAlamatPengguna: String = msgHasilGeocoder.mStringAlamatGabungan
        if (stringAlamatPengguna.isNotEmpty()) {
            mTextViewLokasiPengguna?.text = stringAlamatPengguna
        } else {
            mTextViewLokasiPengguna?.setText(R.string.kartustat_lokasipengguna_belumtersedia)
        }
    }

    override fun setStatusBatasKecepatan() {

        val kodeState: Int = mStateKecepatanBatas.intKodeState
        if (kodeState == StateKonstans.STATE_STATUS_BATAS_KECEPATAN) {
            isKecepatanMelebihiBatas = mStateKecepatanBatas.isBatasMelebihi
            if (isKecepatanMelebihiBatas) {
                mTextViewStatusBatasKecepatan?.setText(R.string.teks_batas_kecepatan_waspada)
                mContext?.let {
                    mTextViewStatusBatasKecepatan?.setTextColor(
                        ContextCompat.getColor(
                            it,
                            R.color.merahnow
                        )
                    )
                }
            } else {
                mTextViewStatusBatasKecepatan?.setText(R.string.teks_batas_kecepatan_aman)
                mContext?.let {
                    mTextViewStatusBatasKecepatan?.setTextColor(
                        ContextCompat.getColor(
                            it,
                            R.color.hijaunow
                        )
                    )
                }
            }
        }
    }

    override fun showToast(intResID: Int) {

        mContext?.let {
            Toast.makeText(it, intResID, Toast.LENGTH_SHORT).show()
        }
    }
}