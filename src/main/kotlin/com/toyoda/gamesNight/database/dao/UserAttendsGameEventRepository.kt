package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.Gamer
import com.toyoda.gamesNight.database.models.GamerAttendsGameEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAttendsGameEventRepository : JpaRepository<GamerAttendsGameEvent, Int> {
    fun deleteByGamerAndEvent(gamer: Gamer, event: GameEvent)
}
