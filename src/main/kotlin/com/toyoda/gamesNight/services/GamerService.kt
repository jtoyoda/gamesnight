package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.GamerRepository
import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.context.annotation.Lazy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import javax.transaction.Transactional
import kotlin.math.floor

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Duplicate Email")
class DuplicateEmailException() : Exception()

val STRING_CHARACTERS = ('0'..'z').toList().toTypedArray()


@Service
@Transactional
class GamerService(private val gamerRepository: GamerRepository,
                   @Lazy private val eventService: GameEventService,
                   private val emailService: EmailService) : AuthService {
    override fun login(email: String, password: String): Gamer {
        return gamerRepository.findByEmailAndPassword(email, password) ?: throw NotAuthorizedException()
    }

    override fun signup(email: String, password: String) {
        val gamer = gamerRepository.findByEmail(email) ?: throw InvalidIdException()
        gamer.password = password
        gamerRepository.save(gamer)
    }

    override fun getUser(tokenFromAuthorizationString: String?): Gamer {
        return gamerRepository.findByToken(tokenFromAuthorizationString ?: throw InvalidIdException())
                ?: throw InvalidIdException()
    }

    fun getGamers(): Set<Gamer> {
        return gamerRepository.findAll().toSet()
    }

    fun createGamer(name: String, email: String): Gamer {
        gamerRepository.findByEmail(email)?.let {
            throw DuplicateEmailException()
        }
        val gamer = Gamer(null, name, email, null, generateToken())
        emailService.sendSignupEmail(gamer.name, gamer.email)
        return gamerRepository.save(gamer)
    }

    fun updateGamer(id: Int, name: String?, email: String?): Gamer {
        val gamer = gamerRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        gamer.name = name ?: gamer.name
        gamer.email = email ?: gamer.email
        return gamerRepository.save(gamer)
    }

    fun deleteGamer(id: Int) {
        val gamer = gamerRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        eventService.removePickerFromAllEvents(gamer)
        gamerRepository.delete(gamer)
    }

    private fun generateToken(): String {
        var passWord = ""
        for (i in 0..31) {
            passWord += STRING_CHARACTERS[floor(Math.random() * STRING_CHARACTERS.size).toInt()]
        }
        return passWord
    }

    fun findByIdIn(attendees: Set<Int>): MutableList<Gamer> {
        return gamerRepository.findAllById(attendees)
    }

    fun findById(id: Int): Gamer? {
        return gamerRepository.findByIdOrNull(id)
    }
}
