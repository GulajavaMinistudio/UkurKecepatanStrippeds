package gulajava.jdwl.locations

import android.location.Location
import com.google.android.gms.location.LocationListener

/**
 * Created by Gulajava Ministudio on 12/29/17.
 */
class LokasiListener : LocationListener {

    var mOnLocationChangeListener: OnLocationChangeListener? = null

    override fun onLocationChanged(locs: Location?) {
        locs?.let {
            mOnLocationChangeListener?.onLocationChanged(it)
        }
    }

    interface OnLocationChangeListener {
        fun onLocationChanged(locations: Location)
    }
}