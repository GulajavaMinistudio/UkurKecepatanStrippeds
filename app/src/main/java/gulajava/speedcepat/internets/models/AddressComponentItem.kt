package gulajava.speedcepat.internets.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
data class AddressComponentItem(
    @SerializedName("long_name")
    var mStringLongName: String = "",
    @SerializedName("short_name")
    var mStringShortName: String = "",
    @SerializedName("types")
    var mListTypes: ArrayList<String> = ArrayList()
)