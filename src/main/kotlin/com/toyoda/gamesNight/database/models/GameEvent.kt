package com.toyoda.gamesNight.database.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.toyoda.gamesNight.controllers.serializers.GamerAttendsGameEventSerializer
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
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
        var gameId: Long?,
        var maxPlayers: Int?,
        @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
        @JsonSerialize(using = GamerAttendsGameEventSerializer::class)
        var attendees: MutableList<GamerAttendsGameEvent>,
        @ManyToOne
        @JoinColumn(name = "picker_id")
        var picker: Gamer?,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "game_night_id")
        @JsonIgnore
        var gameNight: GameNight?
) {
        override fun toString(): String {
                return "GameEvent(name=$name, game=$game, date=$date, attendees=${attendees.map { it.id }})"
        }
}
