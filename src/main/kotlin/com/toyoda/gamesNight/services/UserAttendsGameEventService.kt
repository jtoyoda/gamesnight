package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.UserAttendsGameEventRepository
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.User
import com.toyoda.gamesNight.database.models.UserAttendsGameEvent
import org.springframework.stereotype.Service

@Service
class UserAttendsGameEventService(private val userAttendsGameEventRepository: UserAttendsGameEventRepository,
                                  private val emailService: EmailService) {
    fun inviteUsers(attendees: MutableList<User>, event: GameEvent): MutableList<UserAttendsGameEvent> {
        return attendees.map {inviteUser(it, event)}.toMutableList()
    }

    fun updateInvites(newAttendees: MutableList<User>, oldAttendees: List<User>, event: GameEvent) {
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

    private fun uninviteUser(user: User, event: GameEvent) {
        emailService.uninviteUser(event, user)
        userAttendsGameEventRepository.deleteByUserAndEvent(user, event)

    }

    private fun inviteUser(user: User, event: GameEvent): UserAttendsGameEvent {
        emailService.inviteUser(event, user)
        return userAttendsGameEventRepository.save(UserAttendsGameEvent(null, user, event, false))
    }
}
