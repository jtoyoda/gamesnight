package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.GamerService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/v1/user/nights")
class GamerController(private val gamerService: GamerService) {

    @GetMapping
    fun getGamers(): ResponseEntity<Any> {
        return ResponseEntity.ok(gamerService.getGamers())
    }

    @PostMapping
    fun createGamer(@RequestBody gamerBody: GamerBody): ResponseEntity<Any> {
        return ResponseEntity.ok(gamerService.createGamer(gamerBody.name, gamerBody.email))
    }

    @PutMapping("/{id}")
    fun updateUser(@RequestBody gamerBody: GamerUpdateBody, @PathVariable("id") id: Int): ResponseEntity<Any> {
        return ResponseEntity.ok(gamerService.updateGamer(id, gamerBody.name, gamerBody.email))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable("id") id: Int): ResponseEntity<Any> {
        gamerService.deleteGamer(id)
        return ResponseEntity.noContent().build()
    }

}

data class GamerBody(val name: String, val email: String)

data class GamerUpdateBody(val name: String?, val email: String?)
