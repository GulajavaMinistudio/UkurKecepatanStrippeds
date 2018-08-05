package gulajava.speedcepat.splashscreen

import android.arch.lifecycle.LiveData
import gulajava.speedcepat.database.models.DbSetelan

/**
 * Created by Gulajava Ministudio on 5/13/18.
 */
interface LoadingSplashContract {

    interface View {

        fun initViewModel()

        fun initObservers()

        fun stopObservers()

        fun initDataAwal()

        fun initListener()

        fun initViewWidget()

        fun initTampilan()

        fun cekDatabaseAwal()

        fun pindahHalaman()
    }

    interface Presenter {

        fun initSubscriber()

        fun stopSubscriber()

        fun cekDatabaseAwal()

        fun cekStatusDatabaseAwal(dbSetelan: DbSetelan?)

        fun setDatabaseAwal()

        fun pindahHalaman(isSuksesInit: Boolean)

        fun getStatusInit(): LiveData<Boolean>
    }
}