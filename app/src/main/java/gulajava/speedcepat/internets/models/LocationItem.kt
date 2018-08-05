package gulajava.speedcepat.internets.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
data class LocationItem(
    @SerializedName("lat")
    var mStringLatitude: String = "",
    @SerializedName("lng")
    var mStringLongitude: String = ""
)