package gulajava.speedcepat.database.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Gulajava Ministudio on 5/2/18.
 */
@Entity
data class DbSetelan(
    @Id var id: Long = 0.toLong(),
    var stringKecepatanMaks: String = "70",
    var stringTipeKecepatan: String = "km/jam"
)