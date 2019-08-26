package com.toyoda.gamesNight.database.dao

import com.toyoda.gamesNight.database.models.GameNight
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameNightRepository: JpaRepository<GameNight, Int>
