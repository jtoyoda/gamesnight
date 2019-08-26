package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.GameNightService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.DayOfWeek

@Controller
@RequestMapping("/api/v1/gamesNight/nights")
class GameNightController(private val gamesNightService: GameNightService) {

    @GetMapping
    fun getGameNights(): ResponseEntity<Any> {
        return ResponseEntity.ok(gamesNightService.getNights())
    }

    @PostMapping
    fun createGameNight(@RequestBody gamesNightBody: GamesNightBody): ResponseEntity<Any> {
        return ResponseEntity.ok(gamesNightService.createNight(gamesNightBody.name, gamesNightBody.dayOfWeek,
                gamesNightBody.attendees, gamesNightBody.repeat, gamesNightBody.hour, gamesNightBody.minute))
    }

    @PutMapping("/{id}")
    fun updateGameNight(@RequestBody gamesNightBody: GamesNightUpdateBody, @PathVariable("id") id: Int): ResponseEntity<Any> {
        return ResponseEntity.ok(gamesNightService.updateNight(id, gamesNightBody.name, gamesNightBody.dayOfWeek,
                gamesNightBody.attendees, gamesNightBody.repeat, gamesNightBody.hour, gamesNightBody.minute))
    }

    @DeleteMapping("/{id}")
    fun deleteGameNight(@PathVariable("id") id: Int): ResponseEntity<Any> {
        gamesNightService.deleteGamesNight(id)
        return ResponseEntity.noContent().build()
    }
}

data class GamesNightBody(val name: String, val dayOfWeek: DayOfWeek, val attendees: Set<Int>, val repeat: RepeatEnum,
                          val hour: Int, val minute: Int)

data class GamesNightUpdateBody(val name: String?, val dayOfWeek: DayOfWeek?, val attendees: Set<Int>?, val repeat: RepeatEnum?,
                                val hour: Int?, val minute: Int?)
