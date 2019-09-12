package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.GameNightPicker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameNightPickerRepository: JpaRepository<GameNightPicker, Int> {
    fun findByGameNight(gameNight: GameNight): Set<GameNightPicker>
    fun findByGameNightAndWeek(gameNight: GameNight, week: Long): GameNightPicker?
}
