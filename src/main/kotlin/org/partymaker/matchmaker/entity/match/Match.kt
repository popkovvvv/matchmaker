package org.partymaker.matchmaker.entity.match

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.joda.time.DateTime
import org.joda.time.Seconds
import org.partymaker.matchmaker.entity.Rank
import org.partymaker.matchmaker.entity.player.Player
import java.lang.Double.min
import javax.persistence.*
import kotlin.math.max

@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
@Entity
@Table(name = "matches")
class Match(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @ManyToMany(targetEntity = Player::class, fetch = FetchType.EAGER)
    @JoinTable(
        name = "match_players",
        joinColumns = [JoinColumn(name = "match_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var players: Set<Player> = emptySet(),
    @Type(type = "jsonb") @Column(columnDefinition = "jsonb") var skillStatistics: Statistic = Statistic(),
    @Type(type = "jsonb") @Column(columnDefinition = "jsonb") var latencyStatistic: Statistic = Statistic(),
    @Type(type = "jsonb") @Column(columnDefinition = "jsonb") var timeStatistic: TimeStatistic = TimeStatistic(),
    var startedAt: DateTime? = null,
    @Enumerated(EnumType.ORDINAL)
    var rank: Rank
) {

    fun addPlayer(player: Player) {
        players = players + player
        skillStatistics = skillStatistics.update(
            value = player.skill,
            avg = players.map { it.skill }.average()
        )
        latencyStatistic = latencyStatistic.update(
            value = player.latency,
            avg = players.map { it.latency }.average()
        )

        val durationSeconds = Seconds.secondsBetween(player.startedSearchAt, DateTime.now()).seconds
        timeStatistic = timeStatistic.update(
            durationSeconds
        )
    }

    companion object {

        data class Statistic(
            val min: Double = 0.0,
            val max: Double = 0.0,
            val avg: Double = 0.0
        ) {

            fun update(value: Double, avg: Double) = Statistic(
                min = if (min != 0.0) min(min, value) else value,
                max = if (max != 0.0) max(max, value) else value,
                avg = avg
            )
        }

        data class TimeStatistic(
            val min: Int? = null,
            val max: Int? = null,
            val avg: Int? = null
        ) {

            fun update(value: Int) = TimeStatistic(
                min = if (min != null && min < value) min else value,
                max = if (max != null && max > value) max else value,
                avg = if (min != null && max != null) (min + max) / 2 else null
            )
        }
    }
}
