package gulajava.speedcepat.dialogs

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import gulajava.speedcepat.R
import kotlinx.android.synthetic.main.toolbars.view.*

/**
 * Created by Gulajava Ministudio on 7/29/18.
 */
class BottomDialogBantuanPengukur : BottomSheetDialogFragment() {

    private var mContext: Context? = null
    private var mView: View? = null
    private var mToolbar: Toolbar? = null
    private var mOnMenuItemClickListener: Toolbar.OnMenuItemClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view: View? = inflater.inflate(R.layout.dialog_bottom_bantuan_ukur, container, false)
        mContext = this@BottomDialogBantuanPengukur.context
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view

        initViewWidget()

        initListener()

        initTampilan()
    }

    fun initViewWidget() {

        mToolbar = mView?.toolbar
    }

    fun initListener() {

        mOnMenuItemClickListener = Toolbar.OnMenuItemClickListener { item: MenuItem? ->

            val idMenu: Int = item?.itemId ?: 0

            when (idMenu) {

                R.id.menu_tutup -> {

                    try {
                        this@BottomDialogBantuanPengukur.dialog?.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return@OnMenuItemClickListener true
                }
            }

            return@OnMenuItemClickListener false
        }
    }

    fun initTampilan() {

        mToolbar?.setTitle(R.string.bantuan_pengukur_judul)
        mToolbar?.inflateMenu(R.menu.menu_dialog_tutup)
        mOnMenuItemClickListener?.let { onMenuItemClickListener ->
            mToolbar?.setOnMenuItemClickListener(onMenuItemClickListener)
        }
    }

}