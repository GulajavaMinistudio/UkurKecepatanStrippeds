package gulajava.speedcepat.internets.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
data class GeometryItem(
    @SerializedName("bounds")
    var mBoundLocation: LocationItem = LocationItem(),
    @SerializedName("location")
    var mLocation: LocationItem = LocationItem(),
    @SerializedName("location_type")
    var mStringLocationType: String = "",
    @SerializedName("viewport")
    var mViewPort: ViewPortItem = ViewPortItem()
)