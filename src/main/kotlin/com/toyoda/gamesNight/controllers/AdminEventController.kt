package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.GameEventService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/v1/gamesNight/admin/events")
@CrossOrigin
class AdminEventController(private val gameEventService: GameEventService) {

    @GetMapping
    fun getEvents(): ResponseEntity<Any> {
        return ResponseEntity.ok(gameEventService.getAllFutureEvents())
    }

    @PostMapping
    fun createEvent(@RequestBody event: EventBody): ResponseEntity<Any> {
        if ((event.nights != null && event.attendees != null) || (event.nights == null && event.attendees == null)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Please pick one of night or attendees"))
        }
        return when {
            event.nights != null -> ResponseEntity.ok(gameEventService.createEventWithNights(event.name, event.nights, event.picker, event.date, event.game, event.gameId))
            event.attendees != null -> ResponseEntity.ok(gameEventService.createEventWithAttendees(event.name, event.attendees, event.picker, event.date, event.game, event.gameId))
            else -> ResponseEntity.status(500).build()
        }
    }

    @PutMapping("/{id}")
    fun updateEvent(@RequestBody event: EventUpdateBody, @PathVariable("id") id: Int): ResponseEntity<Any> {
        gameEventService.updateEvent(id, event.name, event.attendees, event.picker, event.date, event.game)
        return ResponseEntity.ok(gameEventService.getEvent(id))
    }

    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable("id") id: Int): ResponseEntity<Any> {
        gameEventService.deleteEvent(id)
        return ResponseEntity.noContent().build()
    }
}

data class EventBody(val name: String, val nights: Set<Int>?, val attendees: Set<Int>?, val picker: Int?, val date: Long, val game: String?, val gameId: Long?)

data class EventUpdateBody(val name: String?, val attendees: Set<Int>?, val picker: Int?, val date: Long?, val game: String?, val gameId: Long?)
