package gulajava.speedcepat.database.repositorys

import gulajava.speedcepat.UkurCepatApp
import gulajava.speedcepat.database.ReposContracts
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.database.models.DbSetelan_
import gulajava.speedcepat.splashscreen.LoadingSplashContract
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.query.Query
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Gulajava Ministudio on 5/13/18.
 */
class RepoLoadingSplash(private val presenter: LoadingSplashContract.Presenter) :
    ReposContracts.RepoLoadingSplash {

    private lateinit var mCompositeDisposable: CompositeDisposable
    private val boxStore: BoxStore = UkurCepatApp.instances.getBoxStoreInstances()

    override fun initSubscriptions() {

        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        } else {
            if (mCompositeDisposable.isDisposed) {
                mCompositeDisposable = CompositeDisposable()
            }
        }
    }

    override fun cekDatabaseSetelan() {

        mCompositeDisposable.add(
            Single.fromCallable {

                val setelanBox: Box<DbSetelan> = boxStore.boxFor(DbSetelan::class.java)
                val setelanQuery: Query<DbSetelan> = setelanBox.query().order(DbSetelan_.id).build()
                var dbSetelan: DbSetelan? = null
                boxStore.runInTx {
                    dbSetelan = setelanQuery.findFirst()
                }

                return@fromCallable dbSetelan
            }
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { dbsetelan: DbSetelan? ->

                        presenter.cekStatusDatabaseAwal(dbsetelan)
                    },
                    { errors: Throwable? ->
                        errors?.printStackTrace()
                        presenter.cekStatusDatabaseAwal(null)
                    }
                )
        )
    }

    override fun addInitNewData(dbSetelan: DbSetelan) {

        mCompositeDisposable.add(
            Single.fromCallable {

                val setelanBox: Box<DbSetelan> = boxStore.boxFor(DbSetelan::class.java)
                setelanBox.put(dbSetelan)

                return@fromCallable true
            }
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _: Boolean? ->
                        presenter.pindahHalaman(true)
                    },
                    { error: Throwable? ->
                        error?.printStackTrace()
                        presenter.pindahHalaman(false)
                    }
                )
        )
    }

    override fun stopSubscriptions() {

        mCompositeDisposable.dispose()
    }
}