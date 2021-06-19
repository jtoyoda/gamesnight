package com.toyoda.gamesNight.services

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Too Many Players added to Game Event")
class TooManyPlayersException() : Exception()
