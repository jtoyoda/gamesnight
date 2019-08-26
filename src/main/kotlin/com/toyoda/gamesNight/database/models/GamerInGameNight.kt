package com.toyoda.gamesNight.database.models

import javax.persistence.*

@Entity
data class GamerInGameNight(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Int?,
        @ManyToOne
        val gamer: Gamer,
        @ManyToOne
        val gameNight: GameNight
)
