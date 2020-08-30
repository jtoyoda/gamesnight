package com.toyoda.gamesNight.database.models

import javax.persistence.*

@Entity
data class GamerAttendsGameEvent(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Int?,
        @ManyToOne(fetch = FetchType.EAGER)
        var gamer: Gamer?,
        @ManyToOne
        var event: GameEvent?,
        var attending: Boolean?,
        var message: String?
)
