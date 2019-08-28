package com.toyoda.gamesNight.database.models

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.toyoda.gamesNight.controllers.RepeatEnum
import com.toyoda.gamesNight.controllers.serializers.GamerInGameNightSerializer
import java.sql.Timestamp
import java.time.DayOfWeek
import javax.persistence.*

@Entity
data class GameNight(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Int?,
        var name: String?,
        @Enumerated(EnumType.STRING)
        var dayOfWeek: DayOfWeek?,
        @Enumerated(EnumType.STRING)
        var repeat: RepeatEnum?,
        var hour: Int?,
        var minute: Int?,
        @OneToMany(mappedBy = "gameNight")
        @JsonSerialize(using = GamerInGameNightSerializer::class)
        var attendees: MutableList<GamerInGameNight>,
        var createdOn: Timestamp?
) {
}
