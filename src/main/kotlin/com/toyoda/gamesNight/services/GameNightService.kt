package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.controllers.RepeatEnum
import com.toyoda.gamesNight.database.dao.GameNightRepository
import com.toyoda.gamesNight.database.models.GameNight
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class GameNightService(private val gameNightRepository: GameNightRepository, private val gamerService: GamerService) {
    fun getNights(): Set<GameNight> {
        return gameNightRepository.findAll().toSet()
    }

    fun createNight(name: String, dayOfWeek: DayOfWeek, attendees: Set<Int>, repeat: RepeatEnum, hour: Int,
                    minute: Int): GameNight {
        val gameNight = GameNight(null, name, dayOfWeek, repeat, hour, minute, gamerService.findByIdIn(attendees), null)
        return gameNightRepository.save(gameNight)
    }

    fun updateNight(id: Int, name: String?, dayOfWeek: DayOfWeek?, attendees: Set<Int>?, repeat: RepeatEnum?,
                    hour: Int, minute: Int): GameNight {
        val gameNight = gameNightRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        gameNight.name = name ?: gameNight.name
        gameNight.dayOfWeek = dayOfWeek ?: dayOfWeek
        if (attendees != null) {
            gameNight.attendees = gamerService.findByIdIn(attendees)
        }
        gameNight.repeat = repeat ?: gameNight.repeat
        gameNight.hour = hour ?: gameNight.hour
        gameNight.minute = minute ?: gameNight.minute
        return gameNightRepository.save(gameNight)
    }

    fun findById(id: Int): GameNight? {
        return gameNightRepository.findByIdOrNull(id)
    }

}
