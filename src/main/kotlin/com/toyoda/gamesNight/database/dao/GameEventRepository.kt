package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameEventRepository : JpaRepository<GameEvent, Int> {
    fun findByAttendeesGamerIn(gamer: Gamer): Set<GameEvent>
    fun findByPicker(gamer: Gamer): Set<GameEvent>
    fun findByGameNightId(gameNightId: Int): Set<GameEvent>
}
