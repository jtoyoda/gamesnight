package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.controllers.RepeatEnum
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.zoneId
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*

const val DEFAULT_HOUR = 18
const val DEFAULT_MINUTE = 30

@Service
class CronService(private val gameNightService: GameNightService, private val gameEventService: GameEventService) {
    @Scheduled(cron = "0 0 12 * * *")
    fun cronJob() {
        val nights = gameNightService.getNights()
        val now = Instant.now()
        val zonedNow = now.atZone(zoneId)

        for (night in nights) {
            val weekNumber = getWeeksElapsed(night, now)

            if (night.dayOfWeek == zonedNow.dayOfWeek
                    && (night.repeat == RepeatEnum.WEEKLY ||
                            (night.repeat == RepeatEnum.BIWEEKLY && (weekNumber % 2) == 0L))) {
                bookEvent(now, night, if (night.repeat == RepeatEnum.WEEKLY) weekNumber else weekNumber/2)
            }
        }
    }

    private fun bookEvent(now: Instant, gameNight: GameNight, weekNumber: Long) {
        gameNight.id?.let { gameNightId ->
            val picker = gameNightService.getPickerForGameNightForWeek(gameNightId, weekNumber + 1)
            // Get next event will return the one today so get the next one
            val date = getNextEvent(now.plus(1, ChronoUnit.DAYS), gameNight)
            gameEventService.createEventWithNight(getName(gameNight, weekNumber), gameNight, picker, date)
        }
    }

    private fun getName(gameNight: GameNight, weekNumber: Long): String {
        return "${gameNight.name}: Week $weekNumber"
    }
}
