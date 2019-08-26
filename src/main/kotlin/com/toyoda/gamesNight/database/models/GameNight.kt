package com.toyoda.gamesNight.database.models

import com.toyoda.gamesNight.controllers.RepeatEnum
import org.springframework.data.annotation.CreatedDate
import java.sql.Timestamp
import java.time.DayOfWeek
import javax.persistence.*

@Entity
data class GameNight(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(columnDefinition = "serial")
        var id: Long?,
        var name: String?,
        @Enumerated(EnumType.STRING)
        var dayOfWeek: DayOfWeek?,
        @Enumerated(EnumType.STRING)
        var repeat: RepeatEnum?,
        var hour: Int?,
        var minute: Int?,
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "gamer_in_game_night",
                joinColumns = [JoinColumn(name = "gamer_id")],
                inverseJoinColumns = [JoinColumn(name = "game_night_id")])
        var attendees: MutableList<Gamer>,
        @CreatedDate
        var createdOn: Timestamp?
) {
}
