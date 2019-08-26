package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.Gamer
import com.toyoda.gamesNight.database.models.GamerInGameNight
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GamerInGameNightRepository: JpaRepository<GamerInGameNight, Int> {
    fun deleteByGamerAndGameNight(oldAttendee: Gamer, gameNight: GameNight)
}
