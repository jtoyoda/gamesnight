package com.toyoda.gamesNight.database.models

import javax.persistence.*

@Entity
data class GameNightPicker(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Int?,
        @ManyToOne
        var gamer: Gamer,
        @ManyToOne
        val gameNight: GameNight,
        val week: Long
)
