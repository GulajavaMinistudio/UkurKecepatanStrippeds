package gulajava.speedcepat.splashscreen

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import gulajava.speedcepat.Konstans
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.database.repositorys.RepoLoadingSplash

/**
 * Created by Gulajava Ministudio on 5/13/18.
 */
class LoadingViewModel : AndroidViewModel, LoadingSplashContract.Presenter {

    private val applicationApp: Application
    private val isInitSetelanOk: MutableLiveData<Boolean> = MutableLiveData()

    private val mRepoLoadingSplash: RepoLoadingSplash

    constructor(application: Application) : super(application) {

        applicationApp = application
        mRepoLoadingSplash = RepoLoadingSplash(this@LoadingViewModel)
        initSubscriber()
    }

    override fun initSubscriber() {

        mRepoLoadingSplash.initSubscriptions()
    }

    override fun stopSubscriber() {

        mRepoLoadingSplash.stopSubscriptions()
    }

    override fun cekDatabaseAwal() {

        mRepoLoadingSplash.cekDatabaseSetelan()
    }

    override fun cekStatusDatabaseAwal(dbSetelan: DbSetelan?) {

        if (dbSetelan != null) {
            pindahHalaman(true)
        } else {
            setDatabaseAwal()
        }
    }

    override fun setDatabaseAwal() {

        val dbSetelan = DbSetelan()
        dbSetelan.stringKecepatanMaks = "70"
        dbSetelan.stringTipeKecepatan = Konstans.STR_KMH
        mRepoLoadingSplash.addInitNewData(dbSetelan)
    }

    override fun pindahHalaman(isSuksesInit: Boolean) {

        isInitSetelanOk.postValue(isSuksesInit)
    }

    override fun onCleared() {
        super.onCleared()
        stopSubscriber()
    }

    override fun getStatusInit(): LiveData<Boolean> {

        return isInitSetelanOk
    }
}