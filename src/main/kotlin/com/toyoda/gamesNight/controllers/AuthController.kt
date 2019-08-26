package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/v1/gamesNight/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("login")
    fun login(@RequestBody loginBody: LoginBody): ResponseEntity<Any> {
        return ResponseEntity.ok(mapOf("token" to authService.login(loginBody.email, loginBody.password)))
    }

    @PutMapping("signup")
    fun signup(@RequestBody signupBody: SignupBody): ResponseEntity<Any> {
        authService.signup(signupBody.email, signupBody.password)
        return ResponseEntity.noContent().build()
    }
}

data class LoginBody(val email: String, val password: String)

data class SignupBody(val email: String, val password: String)
