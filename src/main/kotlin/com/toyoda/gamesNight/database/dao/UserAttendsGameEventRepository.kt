package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.User
import com.toyoda.gamesNight.database.models.UserAttendsGameEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAttendsGameEventRepository: JpaRepository<UserAttendsGameEvent, Int> {
    fun deleteByUserAndEvent(user: User, event: GameEvent)
}
