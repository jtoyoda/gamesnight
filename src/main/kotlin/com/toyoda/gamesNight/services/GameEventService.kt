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
import java.time.temporal.ChronoUnit
import javax.transaction.Transactional

@Service
@Transactional
class GameEventService(private val gameEventRepository: GameEventRepository, private val gamerService: GamerService,
                       private val gameNightService: GameNightService,
                       private val gamerAttendsGameEventService: GamerAttendsGameEventService,
                       private val emailService: EmailService) {
    fun createEventWithAttendees(name: String, attendees: Set<Int>, picker: Int?, date: Long, game: String?, gameId: Long?, maxPlayers: Int?): GameEvent {
        val pickerGamer = if (picker != null) gamerService.findById(picker) else null
        val usersInvited = gamerService.findByIdIn(attendees)
        return createEvent(name, usersInvited, pickerGamer, date, game, gameId, maxPlayers)
    }

    fun createEventWithNights(name: String, nights: Set<Int>, picker: Int?, date: Long, game: String?, gameId: Long?, maxPlayers: Int?): GameEvent {
        val gameNights = gameNightService.findByIdIn(nights)
        val pickerUser = if (picker != null) {
            gamerService.findById(picker)
        } else {
            null
        }
        return createEvent(name, gameNights.flatMap { it.attendees }.map { it.gamer }, pickerUser, date, game, gameId, maxPlayers)
    }

    fun createEventWithNight(name: String, night: GameNight, picker: Gamer?, date: Long): GameEvent {
        return createEvent(name, night.attendees.map { it.gamer }, picker, date, null, null, null, night)
    }

    fun updateEvent(id: Int, name: String?, attendees: Set<Int>?, picker: Int?, date: Long?, game: String?, gameId: Long?, maxPlayers: Int?): GameEvent {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        event.name = name ?: event.name
        var gameChanged = false
        var timeChanged = false
        var pickerChanged = false
        when {
            attendees != null -> gamerAttendsGameEventService.updateInvites(gamerService.findByIdIn(attendees).toMutableList(), event.attendees.mapNotNull { it.gamer }, event)
        }
        if (picker == -1) {
            event.picker = null
        } else if (picker != null && picker != event.picker?.id) {
            event.picker = gamerService.findById(picker)
            pickerChanged = true
        }
        if (date != null) {
            val newTimestamp = Timestamp(date)
            if (newTimestamp != event.date) {
                event.date = newTimestamp
                timeChanged = true
            }
        }
        if (maxPlayers != null) {
            event.maxPlayers = maxPlayers
        }
        if (game != null && game != event.game) {
            event.game = game
            event.gameId = gameId ?: event.gameId
            gameChanged = true
        }
        if (gameChanged || timeChanged) {
            event.attendees.mapNotNull { it.gamer?.email }
                    .map {
                        emailService.notifyEventUpdate(it, event, gameChanged, timeChanged, pickerChanged)
                    }
        }
        if (pickerChanged) {
            event.picker?.email?.let {
                emailService.notifyEventUpdate(it, event, gameChanged, timeChanged, pickerChanged)
            }
        }
        return gameEventRepository.save(event)
    }

    fun getAllFutureEvents(): Set<GameEvent> {
        return gameEventRepository.findAll().filter { gameEvent ->
            gameEvent.date?.after(Timestamp.from(Instant.now().minus(5, ChronoUnit.HOURS))) ?: false
        }.toSet()
    }

    fun deleteEvent(id: Int) {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        event.attendees.mapNotNull { it.gamer?.email }.map { emailService.uninviteUser(it, event) }
        gameEventRepository.delete(event)
    }

    fun getFutureEventsForUser(gamer: Gamer): Set<GameEvent> {
        return gameEventRepository.findByAttendeesGamer(gamer).filter { gameEvent ->
            gameEvent.date?.after(Timestamp(Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli())) ?: false
        }.toSet()
    }

    fun updateEventForGamer(id: Int, gamer: Gamer, attending: Boolean?, game: String?, gameId: Long?, message: String?, maxPlayers: Int?): GamerAttendsGameEvent {
        val event = gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        val userAttendsGameEvent = event.attendees.find { it.gamer == gamer } ?: throw InvalidIdException()
        attending?.let { isAttending ->
            event.maxPlayers?.let { maxPlayers ->
                if (event.attendees.filter { it.attending == true }.size >= maxPlayers && isAttending && userAttendsGameEvent.attending != true) {
                    throw TooManyPlayersException()
                }
            }
            userAttendsGameEvent.attending = isAttending
        }
        message?.let {
            userAttendsGameEvent.message = it
        }
        if (game != null && event.picker == gamer) {
            event.game = game
            event.gameId = gameId ?: event.gameId
            event.attendees.mapNotNull { it.gamer?.email }.map {
                emailService.notifyEventUpdate(it, event,
                        gameChanged = true, timeChanged = false, pickerChanged = false)
            }
        }
        if (maxPlayers != null) {
            event.maxPlayers = maxPlayers
        }
        return userAttendsGameEvent
    }

    fun updateEventForGamer(id: Int, gamerId: Int, attending: Boolean): GamerAttendsGameEvent {
        val gamer = gamerService.findById(gamerId) ?: throw InvalidIdException()
        return updateEventForGamer(id, gamer, attending, null, null, null, null)
    }


    fun getEvent(id: Int): GameEvent {
        return gameEventRepository.findByIdOrNull(id) ?: throw InvalidIdException()
    }

    fun createEvent(name: String, attendees: List<Gamer>, picker: Gamer?, date: Long, game: String?, gameId: Long?, maxPlayers: Int?, night: GameNight? = null): GameEvent {
        val event = gameEventRepository.save(GameEvent(null, name, game, Timestamp(date), gameId, maxPlayers, mutableListOf(), picker, night))
        val usersInvited = gamerAttendsGameEventService.inviteGamers(attendees, event)
        event.attendees = usersInvited
        return gameEventRepository.save(event)
    }

    fun removePickerFromAllEvents(gamer: Gamer) {
        gameEventRepository.findByPicker(gamer).forEach {
            it.picker = null
        }
    }

    fun getFutureEventsByGameNight(gameNightId: Int): Set<GameEvent> {
        val now = Instant.now()
        return gameEventRepository.findByGameNightId(gameNightId).filter {
            it.date?.toInstant()?.let { date -> date > now } ?: false
        }.toSet()
    }
}
