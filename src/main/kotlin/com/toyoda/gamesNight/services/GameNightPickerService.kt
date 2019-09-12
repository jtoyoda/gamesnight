package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.GameNightPickerRepository
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.GameNightPicker
import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class GameNightPickerService(private val gameNightPickerRepository: GameNightPickerRepository) {
    fun getFuturePickersForGameNight(gameNight: GameNight): Map<Long, Gamer> {
        val pickers = gameNightPickerRepository.findByGameNight(gameNight)
        return filterPastWeeks(pickers, gameNight).associate { it.week to it.gamer }
    }

    private fun filterPastWeeks(pickers: Set<GameNightPicker>, gameNight: GameNight): List<GameNightPicker> {
        val endDate = Instant.now()
        val weeksElapsed = getWeeksElapsed(gameNight, endDate)
        return pickers.filter { it.week >= weeksElapsed }
    }

    fun getPickerForGameNightForWeek(gameNight: GameNight, weekNumber: Long): Gamer? {
        return gameNightPickerRepository.findByGameNightAndWeek(gameNight, weekNumber)?.gamer
    }

    fun updatePickerForGameNight(gameNight: GameNight, weekNumber: Long, gamer: Gamer): GameNightPicker {
        val existing = gameNightPickerRepository.findByGameNightAndWeek(gameNight, weekNumber)
        if (existing != null) {
            existing.gamer = gamer
            return existing
        }
        return gameNightPickerRepository.save(GameNightPicker(null, gamer, gameNight, weekNumber))
    }
}

