package gulajava.speedcepat.database

import gulajava.speedcepat.database.models.DbSetelan

/**
 * Created by Gulajava Ministudio on 5/2/18.
 */
interface ReposContracts {

    interface RepoLoadingSplash {

        fun initSubscriptions()

        fun stopSubscriptions()

        fun cekDatabaseSetelan()

        fun addInitNewData(dbSetelan: DbSetelan)

    }

    interface RepoSetelanAplikasi {

        fun initSubscriptions()

        fun stopSubscriptions()

        fun cekDatabaseSetelan()

        fun simpanBatasKecepatan(dbSetelan: DbSetelan)
    }

    interface RepoHitungKecepatan {

        fun initSubscriptions()

        fun stopSubscriptions()

        fun cekDatabaseSetelan()
    }

}