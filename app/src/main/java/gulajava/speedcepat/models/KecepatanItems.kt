package gulajava.speedcepat.models

/**
 * Created by Gulajava Ministudio on 6/11/18.
 */
data class KecepatanItems(
    var mIntKecepatanKmh: Int = 0,
    var mIntKecepatanMph: Int = 0,
    var mIntKecepatanKnot: Int = 0,
    var mDoubleKecepatanMs: Double = 0.toDouble(),
    var mStringBatasKecepatan: String = "70",
    var mStringTipeKecepatanBatas: String = "km/jam"
)