package com.toyoda.gamesNight.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestTemplate

@Controller
@RequestMapping("/api/v1/bgg/search")
@CrossOrigin
class BGGProxy (private val restTemplate: RestTemplate) {
    @GetMapping
    fun get(@RequestParam q: String): ResponseEntity<Any> {
        val response = restTemplate.getForEntity("https://boardgamegeek.com/search/boardgame?q=$q&showcount=20",
                BGGResponse::class.javaObjectType).body
        return if (response != null) ResponseEntity.ok(response)
        else ResponseEntity.badRequest().build()
    }
}

data class BGGResponse(val items: List<BGGItem>)
data class BGGItem(val yearpublished: Int, val objectid: Int, val name: String)
