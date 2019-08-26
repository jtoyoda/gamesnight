package com.toyoda.gamesNight.database.models

import javax.persistence.*

@Entity
data class UserAttendsGameEvent(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Long?,
        @ManyToOne(fetch = FetchType.EAGER)
        var user: User?,
        @ManyToOne
        var event: GameEvent?,
        var attending: Boolean
)
