package com.toyoda.gamesNight.services

import com.toyoda.gamesNight.database.dao.UserRepository
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
class GamerService(val userRepository: UserRepository, private val emailService: EmailService): AuthService {
    override fun login(email: String, password: String): String {
        return userRepository.findByEmailAndPassword(email, password)?.token ?: throw NotAuthorizedException()
    }

    override fun signup(email: String, password: String) {
        val user = userRepository.findByEmailAndPassword(email, password) ?: throw InvalidIdException()
        user.password = password
        userRepository.save(user)
    }

    override fun getUser(tokenFromAuthorizationString: String?): Gamer {
        return userRepository.findByToken(tokenFromAuthorizationString?: throw InvalidIdException())?: throw InvalidIdException()
    }

    fun getGamers(): Set<Gamer> {
        return userRepository.findAll().toSet()
    }

    fun createGamer(name: String, email: String): Gamer {
        userRepository.findByEmail(email)?.let {
            throw DuplicateEmailException()
        }
        val user = Gamer(null, name, email, null, generateToken(), mutableSetOf())
        emailService.sendSignupEmail(user.name, user.email)
        return userRepository.save(user)
    }

    fun updateGamer(id: Int, name: String?, email: String?): Gamer {
        val user = userRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        user.name = name ?: user.name
        user.email = email ?: user.email
        return userRepository.save(user)
    }

    fun deleteGamer(id: Int) {
        val user = userRepository.findByIdOrNull(id) ?: throw InvalidIdException()
        userRepository.delete(user)
    }

    private fun generateToken(): String {
        var passWord = ""
        for (i in 0..31) {
            passWord += STRING_CHARACTERS[floor(Math.random() * STRING_CHARACTERS.size).toInt()]
        }
        return passWord
    }

    fun findByIdIn(attendees: Set<Int>): MutableList<Gamer> {
        return userRepository.findAllById(attendees)
    }

    fun findById(id: Int): Gamer? {
        return userRepository.findByIdOrNull(id)
    }
}
