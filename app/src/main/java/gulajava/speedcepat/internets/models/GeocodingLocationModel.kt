package gulajava.speedcepat.internets.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
data class GeocodingLocationModel(
    @SerializedName("results")
    var mListAddressResult: ArrayList<AddressResultItem> = ArrayList(),
    @SerializedName("status")
    var mStringStatus: String = ""
)