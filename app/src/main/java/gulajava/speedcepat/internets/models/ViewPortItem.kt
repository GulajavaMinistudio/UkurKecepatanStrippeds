package gulajava.speedcepat.internets.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
data class ViewPortItem(
    @SerializedName("northeast")
    var mNorthEast: LocationItem = LocationItem(),
    @SerializedName("southwest")
    var mSouthWest: LocationItem = LocationItem()
)