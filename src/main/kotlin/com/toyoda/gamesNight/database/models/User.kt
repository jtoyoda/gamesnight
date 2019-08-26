package com.toyoda.gamesNight.database.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Long?,
        var name: String?,
        var email: String?,
        @JsonIgnore
        var password: String?,
        @JsonIgnore
        val token: String?,
        @ManyToMany(mappedBy = "attendees", fetch = FetchType.LAZY)
        @JsonIgnore
        val gamesNights: MutableSet<GameNight>
)
