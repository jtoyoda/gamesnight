package com.toyoda.gamesNight

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GamesNightApplication

fun main(args: Array<String>) {
    runApplication<GamesNightApplication>(*args)
}
