package gulajava.speedcepat.setelan

import android.arch.lifecycle.LiveData
import gulajava.speedcepat.database.models.DbSetelan
import gulajava.speedcepat.states.StateData

/**
 * Created by Gulajava Ministudio on 6/14/18.
 */
interface SetelanAplikasiContract {

    interface View {

        fun initViewWidget()

        //INISIALISASI DATA AWAL
        fun initDataAwal()

        //INISIALISASI LISTENER
        fun initListener()

        //INISIALISASI TAMPILAN
        fun initTampilan()

        fun initViewModelObservers()

        fun stopViewModelObservers()

        fun checkStateLiveData(stateData: StateData)

        fun getDataBatasKecepatanDb()

        fun setDataBatasKecepatanDb(dbSetelan: DbSetelan)

        fun getDataIsianKecepatan()

        fun setTipeKecepatan(intPosisi: Int)

        fun simpanBatasKecepatan()

        fun showToast(intResID: Int)
    }

    interface Presenter {

        fun getDbSetelanLiveData(): LiveData<DbSetelan>

        fun getStateViewLiveData(): LiveData<StateData>

        //AKTIFKAN SUBSCRIBER
        fun initSubscriber()

        //HENTIKAN SUBSCRIBER
        fun stopSubscriber()

        fun getDataBatasKecepatan()

        fun setDataBatasKecepatanDb(dbSetelan: DbSetelan)

        fun cekBatasKecepatanIsian(stringBatasKecepatan: String, stringTipeKecepatan: String)

        fun simpanBatasKecepatan(stringBatasKecepatan: String, stringTipeKecepatan: String)

        fun showToast(intResID: Int)
    }
}