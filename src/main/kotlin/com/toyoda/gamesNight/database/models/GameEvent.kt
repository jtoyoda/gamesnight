package com.toyoda.gamesNight.database.models

import java.sql.Timestamp
import javax.persistence.*

@Entity
data class GameEvent(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Int?,
        var name: String?,
        var game: String?,
        var date: Timestamp?,
        @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
        var attendees: MutableList<GamerAttendsGameEvent>,
        @ManyToOne
        @JoinColumn(name = "picker_id")
        var picker: Gamer?
)
