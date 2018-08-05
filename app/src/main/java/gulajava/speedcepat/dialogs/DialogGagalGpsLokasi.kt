package gulajava.speedcepat.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import gulajava.speedcepat.R

/**
 * Created by Gulajava Ministudio on 12/31/17.
 */
class DialogGagalGpsLokasi : DialogFragment() {

    private var mContext: Context? = null

    private var listenerdialogok: DialogInterface.OnClickListener? = null
    private var listenersetelanlokasi: DialogInterface.OnClickListener? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        mContext = this@DialogGagalGpsLokasi.context

        initListener()

        val builder: AlertDialog.Builder? = mContext?.let { context: Context ->
            AlertDialog.Builder(context)
                .setMessage(R.string.toastdialog_pesangagalgps)
                .setPositiveButton(R.string.teks_ok, listenerdialogok)
                .setNeutralButton(R.string.teks_setelanlokasi, listenersetelanlokasi)
        }


        val dialog: Dialog? = builder?.create()
        return dialog as Dialog
    }

    private fun initListener() {

        listenerdialogok = DialogInterface.OnClickListener { _, _ ->
            try {
                this@DialogGagalGpsLokasi.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        listenersetelanlokasi = DialogInterface.OnClickListener { _, _ ->

            //ke setelan lokasi
            try {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                this@DialogGagalGpsLokasi.startActivity(intent)
                this@DialogGagalGpsLokasi.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}