package unit.org.partymaker.matchmaker.builder

import org.joda.time.DateTime
import org.partymaker.matchmaker.entity.Rank
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.match.Match.Companion.Conditions
import org.partymaker.matchmaker.entity.match.Match.Companion.Statistic
import org.partymaker.matchmaker.entity.match.Match.Companion.TimeStatistic
import org.partymaker.matchmaker.entity.player.Player

class MatchBuilder(
    val id: Long? = null,
    var players: Set<Player> = emptySet(),
    var skillStatistics: Statistic = Statistic(),
    var latencyStatistic: Statistic = Statistic(),
    var timeStatistic: TimeStatistic = TimeStatistic(),
    var createdAt: DateTime? = null,
    var startedAt: DateTime? = null,
    var rank: Rank = Rank.MIDDLE,
    var conditions: Conditions? = null
) : Builder<Match> {

    override fun build(): Match = Match(
        id, players, skillStatistics, latencyStatistic, timeStatistic, conditions, createdAt, startedAt, rank
    )
}
