package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/v1/gamesNight/auth")
@CrossOrigin
class AuthController(private val authService: AuthService) {

    @PostMapping("login")
    fun login(@RequestBody loginBody: LoginBody): ResponseEntity<Any> {
        val gamer = authService.login(loginBody.email, loginBody.password)
        val ret = mapOf(
                "name" to gamer.name,
                "email" to gamer.email,
                "id" to gamer.id,
                "token" to gamer.token
        )
        return ResponseEntity.ok(ret)
    }

    @PutMapping("signup")
    fun signup(@RequestBody signupBody: SignupBody): ResponseEntity<Any> {
        authService.signup(signupBody.email, signupBody.password)
        return ResponseEntity.noContent().build()
    }
}

data class LoginBody(val email: String, val password: String)

data class SignupBody(val email: String, val password: String)
