package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<Gamer, Int> {
    fun findByEmailAndPassword(email: String, password: String): Gamer?
    fun findByToken(s: String): Gamer?
    fun findByEmail(email: String): Gamer?
}
