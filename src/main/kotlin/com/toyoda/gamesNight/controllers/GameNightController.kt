package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.GameNightService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.DayOfWeek

@Controller
@RequestMapping("/api/v1/gamesNight/nights")
@CrossOrigin
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

    @GetMapping("/{id}/pickers")
    fun getPickers(@PathVariable("id") id: Int): ResponseEntity<Any> {
        val pickers = gamesNightService.getPickersForGamesNight(id)
        return ResponseEntity.ok(pickers)
    }

    @PutMapping("/{id}/pickers")
    fun setPicker(@PathVariable("id") id: Int, @RequestBody gameNightPickerBody: GameNightPickerBody): ResponseEntity<Any> {
        val picker = gamesNightService.setPickerForNight(id, gameNightPickerBody.gamerId, gameNightPickerBody.weekNumber)
        return if (picker != null) {
            ResponseEntity.ok(picker)
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @GetMapping("/{id}/upcoming")
    fun getUpcomingDates(@PathVariable("id") id: Int): ResponseEntity<Any> {
        return ResponseEntity.ok(gamesNightService.getUpcomingUnscheduledNights(id))
    }
}

data class GamesNightBody(val name: String, val dayOfWeek: DayOfWeek, val attendees: Set<Int>, val repeat: RepeatEnum,
                          val hour: Int, val minute: Int)

data class GamesNightUpdateBody(val name: String?, val dayOfWeek: DayOfWeek?, val attendees: Set<Int>?, val repeat: RepeatEnum?,
                                val hour: Int?, val minute: Int?)

data class GameNightPickerBody(val gamerId: Int, val weekNumber: Long)
