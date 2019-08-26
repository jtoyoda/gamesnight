package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.GameEventRepository
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.Gamer
import com.toyoda.gamesNight.database.models.GamerAttendsGameEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.sql.Timestamp
import javax.transaction.Transactional

@Service
@Transactional
class GameEventService(private val gameEventRepository: GameEventRepository, private val gamerService: GamerService,
                       private val gameNightService: GameNightService,
                       private val gamerAttendsGameEventService: GamerAttendsGameEventService){
    fun createEvent(name: String, attendees: Set<Int>, picker: Int, date: Long): GameEvent {
        val event = gameEventRepository.save(GameEvent(null, name, null, Timestamp(date), mutableListOf(), gamerService.findById(picker)))
        val usersInvited = gamerAttendsGameEventService.inviteGamers(gamerService.findByIdIn(attendees), event)
        event.attendees = usersInvited
        return gameEventRepository.save(event)
    }

    fun createEvent(name: String, night: Int, picker: Int?, date: Long): GameEvent {
        val gameNight =  gameNightService.findById(night) ?: throw InvalidIdException()
        val pickerUser = if(picker != null) {
            gamerService.findById(picker)
        } else {
            null
        }
        return createEvent(name, gameNight, pickerUser, date)
    }

    fun updateEvent(id: Int, name: String?, night: Int?, attendees: Set<Int>?, picker: Int?, date: Long?): GameEvent {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        event.name = name ?: event.name
        when {
            night != null -> {
                val gameNight =  gameNightService.findById(night) ?: throw InvalidIdException()
                gamerAttendsGameEventService.updateInvites(gameNight.attendees, event.attendees.mapNotNull { it.gamer }, event)
            }
            attendees != null -> gamerAttendsGameEventService.updateInvites(gamerService.findByIdIn(attendees).toMutableList(), event.attendees.mapNotNull{ it.gamer }, event)
        }
        if (picker != null) {
            event.picker = gamerService.findById(id)
        }
        if (date != null) {
            event.date = Timestamp(date)
        }
        return gameEventRepository.save(event)
    }

    fun getAllEvents(): Set<GameEvent> {
        return gameEventRepository.findAll().toSet()
    }

    fun deleteEvent(id: Int) {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        gameEventRepository.delete(event)
    }

    fun getEventsForUser(gamer: Gamer): Set<GameEvent> {
        return gameEventRepository.findByAttendeesGamerIn(gamer)
    }

    fun updateEventForUser(id: Int, gamer: Gamer, attending: Boolean?, game: String?): GamerAttendsGameEvent {
        val event =  gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        val userAttendsGameEvent = event.attendees.find { it.gamer == gamer } ?: throw InvalidIdException()
        userAttendsGameEvent.attending = attending ?: userAttendsGameEvent.attending
        if (event.picker == gamer && game != null) {
            event.game = game
        }
        return userAttendsGameEvent
    }

    fun getEvent(id: Int): GameEvent {
        return gameEventRepository.findByIdOrNull(id)?: throw InvalidIdException()
    }

    fun createEvent(name: String, gameNight: GameNight, picker: Gamer?, date: Long): GameEvent {
        val event = gameEventRepository.save(GameEvent(null, name, null, Timestamp(date), mutableListOf(), picker))
        val usersInvited = gamerAttendsGameEventService.inviteGamers(gameNight.attendees, event)
        event.attendees = usersInvited
        return gameEventRepository.save(event)
    }
}
