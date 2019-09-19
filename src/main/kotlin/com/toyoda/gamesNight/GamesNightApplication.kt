package com.toyoda.gamesNight

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import java.time.ZoneId
import java.util.*

@SpringBootApplication
@EnableScheduling
class GamesNightApplication {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }
}


val zoneId: ZoneId = TimeZone.getTimeZone("America/Denver").toZoneId()

fun main(args: Array<String>) {
    runApplication<GamesNightApplication>(*args)
}
