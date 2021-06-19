package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.AuthService
import com.toyoda.gamesNight.services.GameEventService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

const val BEARER_TOKEN = "Bearer"

@Controller
@RequestMapping("/api/v1/gamesNight/events")
@CrossOrigin
class GamerAttendsEventController(
    private val gameEventService: GameEventService,
    private val authService: AuthService
) {
    @GetMapping
    fun getEventsForGamer(@RequestHeader("Authorization") bearerToken: String): ResponseEntity<Any> {
        val user = authService.getUser(getTokenFromAuthorizationString(bearerToken))
        return ResponseEntity.ok(gameEventService.getFutureEventsForUser(user))
    }

    @PutMapping("/{id}")
    fun updateEventForGamer(
        @RequestHeader("Authorization") bearerToken: String,
        @RequestBody respondToEventBody: ResponseToEventBody,
        @PathVariable("id") id: Int
    ): ResponseEntity<Any> {
        val user = authService.getUser(getTokenFromAuthorizationString(bearerToken))
        return ResponseEntity.ok(
            gameEventService.updateEventForGamer(
                id,
                user,
                respondToEventBody.attending,
                respondToEventBody.game,
                respondToEventBody.gameId,
                respondToEventBody.message,
                respondToEventBody.maxPlayers
            )
        )
    }

    /**
     * Extract the OAuth bearer token from a header.
     *
     * @param value The value of the Authorization header
     * @return The token, or null if no OAuth authorization header was supplied.
     */
    protected fun getTokenFromAuthorizationString(value: String): String? {
        if (value.contains(BEARER_TOKEN)) {
            var authHeaderValue = value.substring(BEARER_TOKEN.length).trim { it <= ' ' }
            // Add this here for the auth details later. Would be better to change the signature of this method.
            val commaIndex = authHeaderValue.indexOf(',')
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex)
            }
            return authHeaderValue
        }
        return null
    }
}

data class ResponseToEventBody(
    val game: String?,
    val gameId: Long?,
    val attending: Boolean?,
    val message: String?,
    val maxPlayers: Int?
)
