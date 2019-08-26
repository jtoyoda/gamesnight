package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int> {
    fun findByEmailAndPassword(email: String, password: String): User?
    fun findByToken(s: String): User?
    fun findByEmail(email: String): User?
}
