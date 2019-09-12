package com.toyoda.gamesNight

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.time.ZoneId
import java.util.*

@SpringBootApplication
@EnableScheduling
class GamesNightApplication

val zoneId: ZoneId = TimeZone.getTimeZone("America/Denver").toZoneId()

fun main(args: Array<String>) {
    runApplication<GamesNightApplication>(*args)
}
