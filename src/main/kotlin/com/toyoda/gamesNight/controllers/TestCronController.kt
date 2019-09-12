package com.toyoda.gamesNight.controllers

import com.toyoda.gamesNight.services.CronService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/v1/gamesNight/cron")
class TestCronController(private val cronService: CronService) {
    @GetMapping
    fun get() {
        cronService.cronJob()
    }
}
