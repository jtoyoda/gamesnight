package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid credentials")
class NotAuthorizedException() : Exception()

@Service
interface AuthService {
    fun login(email: String, password: String): Gamer

    fun signup(email: String, password: String)

    fun getUser(tokenFromAuthorizationString: String?): Gamer
}
