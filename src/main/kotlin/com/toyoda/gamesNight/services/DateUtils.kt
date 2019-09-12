package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.zoneId
import java.time.DayOfWeek
import java.time.Instant
import java.time.temporal.ChronoUnit

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

fun getNextEvent(now: Instant, gameNight: GameNight): Long {
    val zonedNow = now.atZone(zoneId)
    return zonedNow
            .plusDays(getDaysToAdd(zonedNow.dayOfWeek, gameNight.dayOfWeek))
            .withHour(gameNight.hour ?: DEFAULT_HOUR)
            .withMinute(gameNight.minute ?: DEFAULT_MINUTE)
            .toInstant().toEpochMilli()
}

private fun getDaysToAdd(currentDate: DayOfWeek, idealDay: DayOfWeek?): Long {
    if (idealDay != null) {
        val numDays = idealDay.ordinal - currentDate.ordinal
        return (if (numDays > 0) numDays else 7 + numDays).toLong()
    }
    return 0
}
