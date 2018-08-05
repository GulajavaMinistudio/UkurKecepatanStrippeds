package gulajava.speedcepat.internets.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Gulajava Ministudio on 6/20/18.
 */
data class AddressResultItem(
    @SerializedName("address_components")
    var mAddressComponentItemList: ArrayList<AddressComponentItem> = ArrayList(),
    @SerializedName("formatted_address")
    var mStringFormattedAddress: String = "",
    @SerializedName("geometry")
    var mGeometryItem: GeometryItem = GeometryItem(),
    @SerializedName("place_id")
    var mStringPlaceID: String = "",
    @SerializedName("types")
    var mListTypeLokasi: ArrayList<String> = ArrayList()
)