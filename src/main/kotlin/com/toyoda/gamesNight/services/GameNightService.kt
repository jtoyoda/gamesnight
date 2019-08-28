package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.controllers.RepeatEnum
import com.toyoda.gamesNight.database.dao.GameNightRepository
import com.toyoda.gamesNight.database.dao.GamerInGameNightRepository
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.GamerInGameNight
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.Instant
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class GameNightService(private val gameNightRepository: GameNightRepository, private val gamerService: GamerService,
                       private val gamerInGameNightRepository: GamerInGameNightRepository) {
    fun getNights(): Set<GameNight> {
        return gameNightRepository.findAll().toSet()
    }

    fun createNight(name: String, dayOfWeek: DayOfWeek, attendees: Set<Int>, repeat: RepeatEnum, hour: Int,
                    minute: Int): GameNight {
        val gamers = gamerService.findByIdIn(attendees)
        val gameNight = GameNight(null, name, dayOfWeek, repeat, hour, minute, mutableListOf(), Timestamp(Instant.now().toEpochMilli()))
        val savedGameNight = gameNightRepository.save(gameNight)
        gamers.forEach {
            savedGameNight.attendees.add(gamerInGameNightRepository.save(GamerInGameNight(null, it, savedGameNight)))
        }
        return savedGameNight

    }

    fun updateNight(id: Int, name: String?, dayOfWeek: DayOfWeek?, attendeeIds: Set<Int>?, repeat: RepeatEnum?,
                    hour: Int?, minute: Int?): GameNight {
        val gameNight = gameNightRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        gameNight.name = name ?: gameNight.name
        gameNight.dayOfWeek = dayOfWeek ?: gameNight.dayOfWeek
        if (attendeeIds != null) {
            val attendees = gamerService.findByIdIn(attendeeIds)
            val currentAttendees = gameNight.attendees.map { it.gamer }
            for (attendee in attendees) {
                if (!currentAttendees.contains(attendee)) {
                    val newGamer = gamerInGameNightRepository.save(GamerInGameNight(null, attendee, gameNight))
                    gameNight.attendees.add(newGamer)
                }
            }
            for (oldAttendee in currentAttendees) {
                if (!attendees.contains(oldAttendee)) {
                    gamerInGameNightRepository.deleteByGamerAndGameNight(oldAttendee, gameNight)
                    gameNight.attendees.removeIf {
                        it.gamer == oldAttendee
                    }
                }
            }
        }
        gameNight.repeat = repeat ?: gameNight.repeat
        gameNight.hour = hour ?: gameNight.hour
        gameNight.minute = minute ?: gameNight.minute
        return gameNightRepository.save(gameNight)
    }

    fun findById(id: Int): GameNight? {
        return gameNightRepository.findByIdOrNull(id)
    }

    fun deleteGamesNight(id: Int) {
        val gameNight = findById(id) ?: throw InvalidIdException()
        gameNightRepository.delete(gameNight)
    }

    fun findByIdIn(nights: Set<Int>): Set<GameNight> {
        return gameNightRepository.findAllById(nights).toSet()
    }

}
