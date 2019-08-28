package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.GamerRepository
import com.toyoda.gamesNight.database.models.Gamer
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
class GamerService(val gamerRepository: GamerRepository, private val emailService: EmailService) : AuthService {
    override fun login(email: String, password: String): Gamer {
        return gamerRepository.findByEmailAndPassword(email, password) ?: throw NotAuthorizedException()
    }

    override fun signup(email: String, password: String) {
        val user = gamerRepository.findByEmail(email) ?: throw InvalidIdException()
        user.password = password
        gamerRepository.save(user)
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
        val user = Gamer(null, name, email, null, generateToken())
        emailService.sendSignupEmail(user.name, user.email)
        return gamerRepository.save(user)
    }

    fun updateGamer(id: Int, name: String?, email: String?): Gamer {
        val user = gamerRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        user.name = name ?: user.name
        user.email = email ?: user.email
        return gamerRepository.save(user)
    }

    fun deleteGamer(id: Int) {
        val user = gamerRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        gamerRepository.delete(user)
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
