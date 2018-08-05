package gulajava.speedcepat.setelan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.R
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKonstans
import gulajava.speedcepat.utils.UtilanView
import kotlinx.android.synthetic.main.incl_setelanaplikasi_keteranganbantuan.*
import kotlinx.android.synthetic.main.setelan_aplikasi.*
import kotlinx.android.synthetic.main.toolbars.*

/**
 * Created by Gulajava Ministudio on 6/18/18.
 */
class SetelanAplikasiActivity : AppCompatActivity(), SetelanAplikasiContract.View {

    private var mContext: Context? = null

    private var mToolbar: Toolbar? = null
    private var mActionBar: ActionBar? = null

    private var mEditTextIsianKecepatan: EditText? = null
    private var mStringIsianBatasKecepatan: String = ""
    private var mStringJenisKecepatan: String = ""

    private var mSpinnerJenisKecepatan: Spinner? = null
    private var mAdapterSpinner: ArrayAdapter<String>? = null
    // private var mStringListJenisKecepatan: ArrayList<String> = ArrayList()
    private var mStringListJenisKecepatan: List<String> = ArrayList()
    private var mSpinnerSelectedListenerJenisKecepatan: AdapterView.OnItemSelectedListener? = null

    private var mButtonSimpanSetelan: Button? = null
    private var mOnClickListenerTombol: View.OnClickListener? = null

    private var mTextViewBantuanJenisKecepatan: TextView? = null

    private var mHandler: Handler? = null
    private var mRunnableJedaInit: Runnable? = null

    // viewmodel dan observer
    private var mSetelanAplikasiViewModel: SetelanAplikasiViewModel? = null
    private lateinit var mDbSetelanObserver: Observer<DbSetelan>
    private lateinit var mStateDataObserver: Observer<StateData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setelan_aplikasi)

        mContext = this@SetelanAplikasiActivity

        initViewWidget()

        mToolbar?.let { this@SetelanAplikasiActivity.setSupportActionBar(it) }
        mActionBar = this@SetelanAplikasiActivity.supportActionBar
        mActionBar?.setTitle(R.string.kartusetel_judul)
        mActionBar?.setDisplayHomeAsUpEnabled(true)

        initListener()

        initDataAwal()

        initTampilan()
    }

    override fun onStart() {
        super.onStart()

        initViewModelObservers()

        mHandler?.postDelayed(mRunnableJedaInit, 450)
    }

    override fun onStop() {
        super.onStop()

        stopViewModelObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val idMenu: Int = item?.itemId ?: 0

        when (idMenu) {

            android.R.id.home -> {
                this@SetelanAplikasiActivity.finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun initViewWidget() {

        mToolbar = toolbar
        mEditTextIsianKecepatan = edit_nilai_batas_kecepatan
        mSpinnerJenisKecepatan = spin_tipe_kecepatan

        mButtonSimpanSetelan = tombol_simpan_setelan
        mTextViewBantuanJenisKecepatan = teks_bantuan_jenis_kecepatan
    }

    override fun initDataAwal() {

        mHandler = Handler()

        mSetelanAplikasiViewModel = ViewModelProviders.of(this@SetelanAplikasiActivity)
            .get(SetelanAplikasiViewModel::class.java)

        val arrayStringKecepatan: Array<String> =
            mContext?.resources?.getStringArray(R.array.array_jeniskecepatan) ?: arrayOf("")
        mStringListJenisKecepatan = arrayStringKecepatan.toMutableList() as? ArrayList<String> ?:
                ArrayList()
        // mStringListJenisKecepatan = arrayStringKecepatan.toList() as? List<String> ?: ArrayList()

        mAdapterSpinner = mContext?.let {
            ArrayAdapter(it, android.R.layout.simple_spinner_item, mStringListJenisKecepatan)
        }
        mAdapterSpinner?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    }

    override fun initListener() {

        mSpinnerSelectedListenerJenisKecepatan = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setTipeKecepatan(position)
            }
        }

        mOnClickListenerTombol = View.OnClickListener { view: View? ->

            val idView: Int = view?.id ?: 0

            if (mContext != null && view != null) {
                UtilanView.sembunyikeyboard(mContext, view)
            }
            when (idView) {

                R.id.tombol_simpan_setelan -> {
                    getDataIsianKecepatan()
                }
            }
        }

        mRunnableJedaInit = Runnable {

            getDataBatasKecepatanDb()
        }

        mDbSetelanObserver = Observer { dbSetelan: DbSetelan? ->

            dbSetelan?.let { this@SetelanAplikasiActivity.setDataBatasKecepatanDb(dbSetelan) }
        }

        mStateDataObserver = Observer { stateData: StateData? ->

            stateData?.let { this@SetelanAplikasiActivity.checkStateLiveData(it) }
        }

    }

    override fun initTampilan() {

        mOnClickListenerTombol?.let { onClickListener ->
            mButtonSimpanSetelan?.setOnClickListener(onClickListener)
        }

        mAdapterSpinner?.let { arrayAdapter: ArrayAdapter<String> ->
            mSpinnerJenisKecepatan?.adapter = arrayAdapter
        }

        mSpinnerSelectedListenerJenisKecepatan?.let { onItemSelectedListener ->
            mSpinnerJenisKecepatan?.onItemSelectedListener = onItemSelectedListener
        }

        mSpinnerJenisKecepatan?.setSelection(0)
        mEditTextIsianKecepatan?.setText("0")
    }

    override fun initViewModelObservers() {

        mSetelanAplikasiViewModel?.getDbSetelanLiveData()
            ?.observe(this@SetelanAplikasiActivity, mDbSetelanObserver)
        mSetelanAplikasiViewModel?.getStateViewLiveData()
            ?.observe(this@SetelanAplikasiActivity, mStateDataObserver)
    }

    override fun stopViewModelObservers() {

        mSetelanAplikasiViewModel?.getDbSetelanLiveData()
            ?.removeObservers(this@SetelanAplikasiActivity)
        mSetelanAplikasiViewModel?.getStateViewLiveData()
            ?.removeObservers(this@SetelanAplikasiActivity)
    }

    override fun checkStateLiveData(stateData: StateData) {

        val kodeState: Int = stateData.intKodeState

        when (kodeState) {

            StateKonstans.STATE_SHOW_TOAST -> {

                val resID: Int = stateData.intPayloadData
                showToast(resID)
            }
        }
    }

    override fun getDataBatasKecepatanDb() {

        mSetelanAplikasiViewModel?.getDataBatasKecepatan()
    }

    override fun setDataBatasKecepatanDb(dbSetelan: DbSetelan) {

        mStringIsianBatasKecepatan = dbSetelan.stringKecepatanMaks
        mEditTextIsianKecepatan?.setText(mStringIsianBatasKecepatan)

        val jenisKecepatan: String = dbSetelan.stringTipeKecepatan
        when (jenisKecepatan) {

            Konstans.STR_KMH -> {
                mSpinnerJenisKecepatan?.setSelection(0)
            }

            Konstans.STR_MPH -> {
                mSpinnerJenisKecepatan?.setSelection(1)
            }

            Konstans.STR_KNT -> {
                mSpinnerJenisKecepatan?.setSelection(2)
            }

            else -> {
                mSpinnerJenisKecepatan?.setSelection(0)
            }
        }
    }

    override fun getDataIsianKecepatan() {

        mStringIsianBatasKecepatan = mEditTextIsianKecepatan?.text?.toString() ?: ""
        simpanBatasKecepatan()
    }

    override fun setTipeKecepatan(intPosisi: Int) {

        when (intPosisi) {

            0 -> {
                mStringJenisKecepatan = Konstans.STR_KMH
            }

            1 -> {
                mStringJenisKecepatan = Konstans.STR_MPH
            }

            2 -> {
                mStringJenisKecepatan = Konstans.STR_KNT
            }
            else -> {
                mStringJenisKecepatan = Konstans.STR_KMH
            }
        }
    }

    override fun simpanBatasKecepatan() {

        mSetelanAplikasiViewModel?.cekBatasKecepatanIsian(
            mStringIsianBatasKecepatan,
            mStringJenisKecepatan
        )
    }

    override fun showToast(intResID: Int) {

        mContext?.let {
            Toast.makeText(it, intResID, Toast.LENGTH_SHORT).show()
        }
    }
}