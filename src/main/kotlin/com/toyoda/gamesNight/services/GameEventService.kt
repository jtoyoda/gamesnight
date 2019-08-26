package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.GameEventRepository
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.User
import com.toyoda.gamesNight.database.models.UserAttendsGameEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.sql.Timestamp
import javax.transaction.Transactional

@Service
@Transactional
class GameEventService(private val gameEventRepository: GameEventRepository, private val userService: UserService,
                       private val gameNightService: GameNightService,
                       private val userAttendsGameEventService: UserAttendsGameEventService){
    fun createEvent(name: String, attendees: Set<Int>, picker: Int, date: Long): GameEvent {
        val event = gameEventRepository.save(GameEvent(null, name, null, Timestamp(date), mutableListOf(), userService.findById(picker)))
        val usersInvited = userAttendsGameEventService.inviteUsers(userService.findByIdIn(attendees), event)
        event.attendees = usersInvited
        return gameEventRepository.save(event)
    }

    fun createEvent(name: String, night: Int, picker: Int?, date: Long): GameEvent {
        val gameNight =  gameNightService.findById(night) ?: throw InvalidIdException()
        val pickerUser = if(picker != null) {
            userService.findById(picker)
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
                userAttendsGameEventService.updateInvites(gameNight.attendees, event.attendees.mapNotNull { it.user }, event)
            }
            attendees != null -> userAttendsGameEventService.updateInvites(userService.findByIdIn(attendees).toMutableList(), event.attendees.mapNotNull{ it.user }, event)
        }
        if (picker != null) {
            event.picker = userService.findById(id)
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

    fun getEventsForUser(user: User): Set<GameEvent> {
        return gameEventRepository.findByAttendeesUserIn(user)
    }

    fun updateEventForUser(id: Int, user: User, attending: Boolean?, game: String?): UserAttendsGameEvent {
        val event =  gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        val userAttendsGameEvent = event.attendees.find { it.user == user } ?: throw InvalidIdException()
        userAttendsGameEvent.attending = attending ?: userAttendsGameEvent.attending
        if (event.picker == user && game != null) {
            event.game = game
        }
        return userAttendsGameEvent
    }

    fun getEvent(id: Int): GameEvent {
        return gameEventRepository.findByIdOrNull(id)?: throw InvalidIdException()
    }

    fun createEvent(name: String, gameNight: GameNight, picker: User?, date: Long): GameEvent {
        val event = gameEventRepository.save(GameEvent(null, name, null, Timestamp(date), mutableListOf(), picker))
        val usersInvited = userAttendsGameEventService.inviteUsers(gameNight.attendees, event)
        event.attendees = usersInvited
        return gameEventRepository.save(event)
    }
}
