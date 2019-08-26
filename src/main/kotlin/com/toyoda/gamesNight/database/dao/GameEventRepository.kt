package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameEventRepository: JpaRepository<GameEvent, Int> {
    fun findByAttendeesUserIn(user: User): Set<GameEvent>
}
