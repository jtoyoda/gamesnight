package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.UserAttendsGameEventRepository
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.Gamer
import com.toyoda.gamesNight.database.models.GamerAttendsGameEvent
import org.springframework.stereotype.Service

@Service
class GamerAttendsGameEventService(private val userAttendsGameEventRepository: UserAttendsGameEventRepository,
                                   private val emailService: EmailService) {
    fun inviteGamers(attendees: MutableList<Gamer>, event: GameEvent): MutableList<GamerAttendsGameEvent> {
        return attendees.map {inviteUser(it, event)}.toMutableList()
    }

    fun updateInvites(newAttendees: MutableList<Gamer>, oldAttendees: List<Gamer>, event: GameEvent) {
        for (newAttendee in newAttendees) {
            if (!oldAttendees.contains(newAttendee)) {
                inviteUser(newAttendee, event)
            }
        }
        for (oldAttendee in oldAttendees) {
            if (!newAttendees.contains(oldAttendee)) {
                uninviteUser(oldAttendee, event)
            }
        }
    }

    private fun uninviteUser(gamer: Gamer, event: GameEvent) {
        emailService.uninviteUser(event, gamer)
        userAttendsGameEventRepository.deleteByUserAndEvent(gamer, event)

    }

    private fun inviteUser(gamer: Gamer, event: GameEvent): GamerAttendsGameEvent {
        emailService.inviteUser(event, gamer)
        return userAttendsGameEventRepository.save(GamerAttendsGameEvent(null, gamer, event, false))
    }
}
