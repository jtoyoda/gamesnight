package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.GameEventRepository
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.GameNight
import com.toyoda.gamesNight.database.models.Gamer
import com.toyoda.gamesNight.database.models.GamerAttendsGameEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import javax.transaction.Transactional

@Service
@Transactional
class GameEventService(private val gameEventRepository: GameEventRepository, private val gamerService: GamerService,
                       private val gameNightService: GameNightService,
                       private val gamerAttendsGameEventService: GamerAttendsGameEventService,
                       private val emailService: EmailService) {
    fun createEventWithAttendees(name: String, attendees: Set<Int>, picker: Int?, date: Long, game: String?): GameEvent {
        val pickerGamer = if (picker != null) gamerService.findById(picker) else null
        val usersInvited = gamerService.findByIdIn(attendees)
        return createEvent(name, usersInvited, pickerGamer, date, game)
    }

    fun createEventWithNights(name: String, nights: Set<Int>, picker: Int?, date: Long, game: String?): GameEvent {
        val gameNights = gameNightService.findByIdIn(nights)
        val pickerUser = if (picker != null) {
            gamerService.findById(picker)
        } else {
            null
        }
        return createEvent(name, gameNights.flatMap { it.attendees }.map { it.gamer }, pickerUser, date, game)
    }

    fun createEventWithNight(name: String, night: GameNight, picker: Gamer?, date: Long): GameEvent {
        return createEvent(name, night.attendees.map { it.gamer }, picker, date, null)
    }

    fun updateEvent(id: Int, name: String?, attendees: Set<Int>?, picker: Int?, date: Long?, game: String?): GameEvent {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        event.name = name ?: event.name
        when {
            attendees != null -> gamerAttendsGameEventService.updateInvites(gamerService.findByIdIn(attendees).toMutableList(), event.attendees.mapNotNull { it.gamer }, event)
        }
        if (picker != null) {
            event.picker = gamerService.findById(id)
        }
        if (date != null) {
            event.date = Timestamp(date)
        }
        if (game != null) {
            updateGameForEvent(event, game)
        }
        return gameEventRepository.save(event)
    }

    fun getAllEvents(): Set<GameEvent> {
        return gameEventRepository.findAll().toSet()
    }

    fun deleteEvent(id: Int) {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        event.attendees.mapNotNull { it.gamer?.email }.map { emailService.uninviteUser(it, event) }
        gameEventRepository.delete(event)
    }

    fun getFutureEventsForUser(gamer: Gamer): Set<GameEvent> {
        return gameEventRepository.findByAttendeesGamerIn(gamer).filter { gameEvent ->
            gameEvent.date?.after(Timestamp(Instant.now().toEpochMilli())) ?: false
        }.toSet()
    }

    fun updateEventForUser(id: Int, gamer: Gamer, attending: Boolean?, game: String?): GamerAttendsGameEvent {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        val userAttendsGameEvent = event.attendees.find { it.gamer == gamer } ?: throw InvalidIdException()
        userAttendsGameEvent.attending = attending ?: userAttendsGameEvent.attending
        if (game != null && event.picker == gamer) {
            updateGameForEvent(event, game)
        }
        return userAttendsGameEvent
    }

    fun getEvent(id: Int): GameEvent {
        return gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
    }

    private fun updateGameForEvent(event: GameEvent, game: String) {
        event.game = game
        event.attendees.mapNotNull { it.gamer?.email }.map { emailService.notifyGameUpdate(it, event) }
    }

    fun createEvent(name: String, attendees: List<Gamer>, picker: Gamer?, date: Long, game: String?): GameEvent {
        val event = gameEventRepository.save(GameEvent(null, name, game, Timestamp(date), mutableListOf(), picker))
        val usersInvited = gamerAttendsGameEventService.inviteGamers(attendees, event)
        event.attendees = usersInvited
        return gameEventRepository.save(event)
    }

    fun removePickerFromAllEvents(gamer: Gamer) {
        gameEventRepository.findByPicker(gamer).forEach {
            it.picker = null
        }
    }
}
