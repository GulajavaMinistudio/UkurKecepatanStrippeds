package gulajava.speedcepat.utils

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.util.*

/**
 * Created by Gulajava Ministudio on 12/24/17.
 */
class UtilanView {

    companion object Factory {

        private var screenWidth = 0

        fun getScreenWidth(c: Context?): Int {
            if (screenWidth == 0) {
                try {
                    val wm = c?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                    val display = wm?.defaultDisplay
                    val size = Point()
                    display?.getSize(size)
                    screenWidth = size.x
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return screenWidth
        }


        //MENAMPILKAN MENU ACTION BAR
        fun munculMenuAction(context: Context?) {

            try {
                val config = ViewConfiguration.get(context)
                val menuKey = ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")

                if (menuKey != null) {
                    menuKey.isAccessible = true
                    menuKey.setBoolean(config, false)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }

        //SEMBUNYIKAN KEYBOARD
        fun sembunyikeyboard(context: Context?, view: View?) {

            try {
                val manager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                manager?.hideSoftInputFromWindow(
                    view?.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        //TEXTVIEW SEPARATOR
        fun formatAngkaPisah(angkastr: String?): String {

            var bilanganpisah: String

            try {
                val intangka = Integer.valueOf(angkastr)
                // bilanganpisah = String.format(Locale.getDefault(), "%,d", angkastr).replace(",", ".");
                bilanganpisah = String.format(Locale.getDefault(), "%,d", intangka)
            } catch (e: Exception) {
                e.printStackTrace()
                bilanganpisah = "" + angkastr
            }

            return bilanganpisah
        }
    }
}