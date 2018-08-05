package gulajava.speedcepat.hitungkecepatan.fragmenttabs

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.R
import gulajava.speedcepat.dataparsers.KecepatanParsers
import gulajava.speedcepat.models.BarKecepatanTampilItems
import gulajava.speedcepat.models.KecepatanItems
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKonstans
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Gulajava Ministudio on 7/20/18.
 */
class FragTabKecepatanViewModel : AndroidViewModel, FragTabKecepatanContract.Presenter {

    private var mApplication: Application
    private lateinit var mCompositeDisposable: CompositeDisposable

    private val mLiveDataBarKecepatanItems: MutableLiveData<BarKecepatanTampilItems> =
        MutableLiveData()

    private val mLiveDataStateView: MutableLiveData<StateData> = MutableLiveData()

    private val mPublishSubjectHitungBarTampilan: PublishSubject<KecepatanItems> =
        PublishSubject.create()
    private val mKecepatanParsers: KecepatanParsers

    private var mDoubleBatasKecepatan: Double = 70.0
    private var mDoubleBarTampil: Double = 0.0
    private var mDoubleBarTampilBg: Double = 0.0
    private var mBarKecepatanTampilItems: BarKecepatanTampilItems = BarKecepatanTampilItems()

    private var mStringTipeFragment: String = Konstans.STR_KMH

    constructor(application: Application) : super(application) {

        mApplication = application
        mKecepatanParsers =
                KecepatanParsers(mApplication.applicationContext)
    }

    override fun getLiveDataBarKecepatanItem(): LiveData<BarKecepatanTampilItems> {

        return mLiveDataBarKecepatanItems
    }

    override fun getLiveDataStateView(): LiveData<StateData> {

        return mLiveDataStateView
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
            mPublishSubjectHitungBarTampilan
                .observeOn(Schedulers.single())
                .map { kecepatanitem: KecepatanItems ->

                    val stringBatasKecepatan: String = kecepatanitem.mStringBatasKecepatan
                    val doubleBatasKecepatan = stringBatasKecepatan.toDoubleOrNull() ?: 0.0
                    val doubleBatasKecepatanMs: Double
                    val doubleKecepatanMs: Double = kecepatanitem.mDoubleKecepatanMs
                    val stringTipeBatasKecepatan: String = kecepatanitem.mStringTipeKecepatanBatas

                    // samakan semua batas kecepatan ke bentuk batas kecepatan mph
                    when (stringTipeBatasKecepatan) {

                        Konstans.STR_KMH -> {

                            doubleBatasKecepatanMs =
                                    mKecepatanParsers.convertKmhToMS(doubleBatasKecepatan)
                        }

                        Konstans.STR_MPH -> {

                            doubleBatasKecepatanMs =
                                    mKecepatanParsers.convertMphToMS(doubleBatasKecepatan)
                        }

                        Konstans.STR_KNT -> {

                            doubleBatasKecepatanMs =
                                    mKecepatanParsers.convertKnotToMS(doubleBatasKecepatan)
                        }

                        else -> {
                            doubleBatasKecepatanMs =
                                    mKecepatanParsers.convertKmhToMS(doubleBatasKecepatan)
                        }
                    }

                    // konversi sesuai tipe fragment
                    when (mStringTipeFragment) {

                        Konstans.STR_KMH -> {

                            mDoubleBarTampil = mKecepatanParsers.cariCepatanKmh(doubleKecepatanMs)
                            mDoubleBatasKecepatan =
                                    mKecepatanParsers.cariCepatanKmh(doubleBatasKecepatanMs)
                        }

                        Konstans.STR_MPH -> {

                            mDoubleBarTampil = mKecepatanParsers.cariCepatanMph(doubleKecepatanMs)
                            mDoubleBatasKecepatan =
                                    mKecepatanParsers.cariCepatanMph(doubleBatasKecepatanMs)
                        }

                        Konstans.STR_KNT -> {

                            mDoubleBarTampil = mKecepatanParsers.cariCepatanKnot(doubleKecepatanMs)
                            mDoubleBatasKecepatan =
                                    mKecepatanParsers.cariCepatanKnot(doubleBatasKecepatanMs)
                        }

                        else -> {

                            mDoubleBarTampil = mKecepatanParsers.cariCepatanKmh(doubleKecepatanMs)
                            mDoubleBatasKecepatan =
                                    mKecepatanParsers.cariCepatanKmh(doubleBatasKecepatanMs)
                        }
                    }

                    if (mDoubleBarTampil >= 0 && mDoubleBarTampil < mDoubleBatasKecepatan) {

                        mDoubleBarTampilBg = mDoubleBatasKecepatan - mDoubleBarTampil
                    } else if (mDoubleBarTampil < 0) {

                        mDoubleBarTampil = 1.0
                        mDoubleBarTampilBg = 150.0
                    } else if (mDoubleBarTampil >= mDoubleBatasKecepatan) {
                        mDoubleBarTampil = mDoubleBatasKecepatan
                        mDoubleBarTampilBg = 0.0
                    }

                    return@map true
                }
                .map { _: Boolean ->

                    val mBarKecepatanTampilItems = BarKecepatanTampilItems()
                    mBarKecepatanTampilItems.mDoubleBarTampil = mDoubleBarTampil
                    mBarKecepatanTampilItems.mDoubleBarTampilBg = mDoubleBarTampilBg
                    return@map mBarKecepatanTampilItems
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { barkecepatanitem: BarKecepatanTampilItems? ->

                        if (barkecepatanitem != null) {
                            mBarKecepatanTampilItems = barkecepatanitem
                            mLiveDataBarKecepatanItems.postValue(mBarKecepatanTampilItems)
                        }
                    },
                    { error: Throwable? ->
                        error?.printStackTrace()
                        showToast(R.string.toast_gagal_tampil_bataskecepatan)
                        restartSubscriber()
                    }
                )
        )
    }

    override fun setTipeBatasKecepatanFragment(stringTipeFragment: String) {

        mStringTipeFragment = stringTipeFragment
    }

    override fun hitungBatasKecepatanBarTampil(kecepatanItems: KecepatanItems) {

        if (mPublishSubjectHitungBarTampilan.hasObservers()) {
            mPublishSubjectHitungBarTampilan.onNext(kecepatanItems)
        }
    }

    override fun showToast(intResID: Int) {

        val stateData = StateData()
        stateData.intKodeState = StateKonstans.STATE_SHOW_TOAST
        stateData.intPayloadData = intResID
        mLiveDataStateView.postValue(stateData)
    }
}