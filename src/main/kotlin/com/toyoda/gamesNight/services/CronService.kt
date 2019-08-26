package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.controllers.RepeatEnum
import com.toyoda.gamesNight.database.models.GameNight
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.IsoFields

@Service
class CronService(private val gameNightService: GameNightService, private val gameEventService: GameEventService) {
    @Scheduled(cron = "@daily")
    fun cronJob() {
        val nights = gameNightService.getNights()
        val now = Instant.now()
        val zonedNow = now.atZone(ZoneOffset.systemDefault())

        for(night in nights) {
            val d1i = Instant.ofEpochMilli(night.createdOn!!.time)

            val startDate = LocalDateTime.ofInstant(d1i, ZoneId.systemDefault())
            val endDate = LocalDateTime.ofInstant(now, ZoneId.systemDefault())

            val weekNumber = ChronoUnit.WEEKS.between(startDate, endDate)
            if(night.dayOfWeek == zonedNow.dayOfWeek
                        && (night.repeat == RepeatEnum.WEEKLY ||
                        (night.repeat == RepeatEnum.BIWEEKLY && (weekNumber % 2) == 0L))) {
                bookEvent(now, night, weekNumber)
            }
        }
    }

    private fun bookEvent(now: Instant, gameNight: GameNight, weekNumber: Long) {
        val picker = if (gameNight.attendees.isEmpty()) {
            gameNight.attendees[(weekNumber % gameNight.attendees.size).toInt()]
        } else {
            null
        }
        val zonedNow = now.atZone(ZoneId.systemDefault())
        val date = zonedNow
                .plusDays(7)
                .withHour(gameNight.hour ?: 18)
                .withMinute(gameNight.minute ?: 30)
                .toInstant().toEpochMilli()
        gameEventService.createEvent(getName(gameNight, zonedNow), gameNight, picker, date)
    }

    private fun getName(gameNight: GameNight, now: ZonedDateTime): String {
        return "${gameNight.name}: Week ${now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)}"
    }
}
