package gulajava.speedcepat.hitungkecepatan.fragmenttabs

import android.arch.lifecycle.LiveData
import gulajava.speedcepat.models.BarKecepatanTampilItems
import gulajava.speedcepat.models.KecepatanItems
import gulajava.speedcepat.states.StateData

/**
 * Created by Gulajava Ministudio on 6/24/18.
 */
interface FragTabKecepatanContract {

    interface View {

        fun initDataAwal()

        fun initListener()

        fun initViewWidget()

        fun initTampilan()

        fun initViewModelObservers()

        fun stopViewModelObservers()

        fun initSubscriber()

        fun stopSubscriber()

        fun restartSubscriber()

        fun initPublishSubjectSubscriber()

        fun hitungBarTampilanKecepatan()

        fun setTampilanKecepatan()

        fun setTeksLokasiPengguna()

        fun setStatusBatasKecepatan()

        fun showToast(intResID: Int)
    }

    interface Presenter {

        fun getLiveDataBarKecepatanItem(): LiveData<BarKecepatanTampilItems>

        fun getLiveDataStateView(): LiveData<StateData>

        fun initSubscriber()

        fun stopSubscriber()

        fun restartSubscriber()

        fun initPublishSubjectSubscriber()

        fun setTipeBatasKecepatanFragment(stringTipeFragment: String)

        fun hitungBatasKecepatanBarTampil(kecepatanItems: KecepatanItems)

        fun showToast(intResID: Int)
    }
}