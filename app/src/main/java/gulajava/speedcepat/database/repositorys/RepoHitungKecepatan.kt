package gulajava.speedcepat.database.repositorys

import gulajava.speedcepat.R
import gulajava.speedcepat.UkurCepatApp
import gulajava.speedcepat.database.ReposContracts
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.database.models.DbSetelan_
import gulajava.speedcepat.hitungkecepatan.HitungKecepatanContract
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.query.Query
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Gulajava Ministudio on 6/23/18.
 */
class RepoHitungKecepatan(private val mPresenter: HitungKecepatanContract.Presenter) :
    ReposContracts.RepoHitungKecepatan {

    private lateinit var mCompositeDisposable: CompositeDisposable
    private val mBoxStore: BoxStore = UkurCepatApp.instances.getBoxStoreInstances()

    override fun initSubscriptions() {

        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        } else {
            if (mCompositeDisposable.isDisposed) {
                mCompositeDisposable = CompositeDisposable()
            }
        }
    }

    override fun stopSubscriptions() {

        mCompositeDisposable.dispose()
    }

    override fun cekDatabaseSetelan() {

        mCompositeDisposable.add(
            Single.fromCallable {

                val setelanBox: Box<DbSetelan> = mBoxStore.boxFor(DbSetelan::class.java)
                val setelanBoxQuery: Query<DbSetelan> =
                    setelanBox.query().order(DbSetelan_.id).build()

                var dbSetelan: DbSetelan? = null
                mBoxStore.runInTx {
                    dbSetelan = setelanBoxQuery.findFirst()
                }

                return@fromCallable dbSetelan
            }
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { dbsetelan: DbSetelan? ->

                        if (dbsetelan != null) {
                            mPresenter.setDataBatasKecepatanDb(dbsetelan)
                        } else {
                            mPresenter.tampilPesanPeringatan(R.string.toast_gagal_dbsetelan_hitungkecepatan)
                        }
                    },
                    { error: Throwable? ->
                        error?.printStackTrace()
                        mPresenter.tampilPesanPeringatan(R.string.toast_gagal_dbsetelan_hitungkecepatan)
                    }
                )
        )
    }
}