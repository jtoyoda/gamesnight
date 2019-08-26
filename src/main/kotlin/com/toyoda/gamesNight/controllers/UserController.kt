package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/v1/user/nights")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getUsers(): ResponseEntity<Any> {
        return ResponseEntity.ok(userService.getUsers())
    }

    @PostMapping
    fun createUser(@RequestBody userBody: UserBody): ResponseEntity<Any> {
        return ResponseEntity.ok(userService.createUser(userBody.name, userBody.email))
    }

    @PutMapping("/{id}")
    fun updateUser(@RequestBody userBody: UserUpdateBody, @PathVariable("id") id: Int): ResponseEntity<Any> {
        return ResponseEntity.ok(userService.updateUser(id, userBody.name, userBody.email))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable("id") id: Int): ResponseEntity<Any> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

}

data class UserBody(val name: String, val email: String)

data class UserUpdateBody(val name: String?, val email: String?)
