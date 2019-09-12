package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.GameNightPickerRepository
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.GameNightPicker
import com.toyoda.gamesNight.database.models.Gamer
import com.toyoda.gamesNight.zoneId
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.Instant
import java.time.temporal.ChronoUnit

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

fun getWeeksElapsed(gameNight: GameNight, endInstant: Instant): Long {
    val startInstant = Instant.ofEpochMilli(gameNight.createdOn!!.time)
    val startDate = startInstant.atZone(zoneId)
    val firstEvent = startDate
            .withHour(gameNight.hour ?: DEFAULT_HOUR)
            .withMinute(gameNight.minute ?: DEFAULT_MINUTE)
            .plusDays(getDaysToAdd(startDate.dayOfWeek, gameNight.dayOfWeek))
    val endDate = endInstant.atZone(zoneId)
    return ChronoUnit.WEEKS.between(firstEvent, endDate)
}

private fun getDaysToAdd(currentDate: DayOfWeek, idealDay: DayOfWeek?): Long {
    if (idealDay != null) {
        val numDays = idealDay.ordinal - currentDate.ordinal
        return (if (numDays > 0) numDays else 7 + numDays).toLong()
    }
    return 0
}
