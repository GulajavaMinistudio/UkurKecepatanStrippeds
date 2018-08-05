package gulajava.speedcepat.setelan

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.R
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.database.repositorys.RepoSetelanAplikasi
import gulajava.speedcepat.states.StateData
import gulajava.speedcepat.states.StateKonstans
import gulajava.speedcepat.dataparsers.DataParsers

/**
 * Created by Gulajava Ministudio on 6/17/18.
 */
class SetelanAplikasiViewModel : AndroidViewModel, SetelanAplikasiContract.Presenter {

    private val mRepoSetelanAplikasi: RepoSetelanAplikasi =
        RepoSetelanAplikasi(this@SetelanAplikasiViewModel)
    private val mDbSetelanLiveData: MutableLiveData<DbSetelan> = MutableLiveData()
    private val mStatesViewLiveData: MutableLiveData<StateData> = MutableLiveData()
    private var mDbSetelan: DbSetelan = DbSetelan()

    private val mDataParsers: DataParsers

    constructor(application: Application) : super(application) {

        mDataParsers = DataParsers(application.applicationContext)
        initSubscriber()
    }

    override fun getDbSetelanLiveData(): LiveData<DbSetelan> {

        return mDbSetelanLiveData
    }

    override fun getStateViewLiveData(): LiveData<StateData> {

        return mStatesViewLiveData
    }

    override fun initSubscriber() {

        mRepoSetelanAplikasi.initSubscriptions()
    }

    override fun stopSubscriber() {

        mRepoSetelanAplikasi.stopSubscriptions()
    }

    override fun getDataBatasKecepatan() {

        mRepoSetelanAplikasi.cekDatabaseSetelan()
    }

    override fun setDataBatasKecepatanDb(dbSetelan: DbSetelan) {

        mDbSetelan = dbSetelan
        mDbSetelanLiveData.postValue(mDbSetelan)
    }

    override fun cekBatasKecepatanIsian(stringBatasKecepatan: String, stringTipeKecepatan: String) {

        if (stringTipeKecepatan.isNotEmpty()) {

            if (stringBatasKecepatan.isNotEmpty()) {

                val batasKecepatanDouble: Double =
                    stringBatasKecepatan.toDoubleOrNull() ?: 0.toDouble()

                when (stringTipeKecepatan) {

                    Konstans.STR_KMH -> {

                        if (batasKecepatanDouble >= Konstans.DOUBLE_BATAS_BAWAH_KMH
                            && batasKecepatanDouble <= Konstans.DOUBLE_BATAS_ATAS_KMH) {

                            simpanBatasKecepatan(stringBatasKecepatan, stringTipeKecepatan)
                        } else {
                            showToast(R.string.toast_gagal_setelan_nilaikecepatan_kmh)
                        }
                    }

                    Konstans.STR_MPH -> {

                        if (batasKecepatanDouble >= Konstans.DOUBLE_BATAS_BAWAH_MPH
                            && batasKecepatanDouble <= Konstans.DOUBLE_BATAS_ATAS_MPH) {

                            simpanBatasKecepatan(stringBatasKecepatan, stringTipeKecepatan)
                        } else {
                            showToast(R.string.toast_gagal_setelan_nilaikecepatan_mph)
                        }
                    }

                    Konstans.STR_KNT -> {

                        if (batasKecepatanDouble >= Konstans.DOUBLE_BATAS_BAWAH_KNOT
                            && batasKecepatanDouble <= Konstans.DOUBLE_BATAS_ATAS_KNOT) {

                            simpanBatasKecepatan(stringBatasKecepatan, stringTipeKecepatan)
                        } else {
                            showToast(R.string.toast_gagal_setelan_nilaikecepatan_knot)
                        }
                    }

                    else -> {
                        showToast(R.string.toast_gagal_setelan_satuankecepatan)
                    }
                }
            } else {
                showToast(R.string.toast_gagal_setelan_nilaikecepatan)
            }
        } else {
            showToast(R.string.toast_gagal_setelan_satuankecepatan)
        }

    }

    override fun simpanBatasKecepatan(stringBatasKecepatan: String, stringTipeKecepatan: String) {

        val batasKecepatanInt: Int =
            mDataParsers.convertPembulatanBilanganKeAtas(stringBatasKecepatan, 0)

        mDbSetelan.stringKecepatanMaks = batasKecepatanInt.toString()
        mDbSetelan.stringTipeKecepatan = stringTipeKecepatan
        mRepoSetelanAplikasi.simpanBatasKecepatan(mDbSetelan)
    }

    override fun showToast(intResID: Int) {

        val stateData = StateData()
        stateData.intKodeState = StateKonstans.STATE_SHOW_TOAST
        stateData.intPayloadData = intResID
        mStatesViewLiveData.value = stateData
    }

    override fun onCleared() {
        super.onCleared()
        stopSubscriber()
    }
}