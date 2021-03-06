package com.toyoda.gamesNight.database.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class Gamer(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Int?,
        var name: String?,
        var email: String?,
        @JsonIgnore
        var password: String?,
        @JsonIgnore
        val token: String?
)
