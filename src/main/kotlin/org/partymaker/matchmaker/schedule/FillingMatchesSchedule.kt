package org.partymaker.matchmaker.schedule

import mu.KotlinLogging
import org.partymaker.matchmaker.entity.Rank
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class FillingMatchesSchedule(
    private val playerRepository: PlayerRepository,
    private val matchRepository: MatchRepository,
    @Value("\${match.group.size}") private val matchGroupSize: Int,
) : Schedule {

    private val logger = KotlinLogging.logger { }

    @Scheduled(fixedRate = 5000)
    override fun run() {
        val players = playerRepository.findSearchMatchPlayers()
        val lowSkilledPlayersSize = players.filter { it.rank == Rank.LOW }.size
        val middleSkilledPlayersSize = players.filter { it.rank == Rank.MIDDLE }.size
        val highSkilledPlayersSize = players.filter { it.rank == Rank.HIGH }.size

        val lowSkilledMatchesSize = ceil(lowSkilledPlayersSize.toDouble() / matchGroupSize.toDouble()).toInt()
        val middleSkilledMatchesSize = ceil(middleSkilledPlayersSize.toDouble() / matchGroupSize.toDouble()).toInt()
        val highSkilledMatchesSize = ceil(highSkilledPlayersSize.toDouble() / matchGroupSize.toDouble()).toInt()

        createMatchesForRank(lowSkilledMatchesSize, Rank.LOW)
        createMatchesForRank(middleSkilledMatchesSize, Rank.MIDDLE)
        createMatchesForRank(highSkilledMatchesSize, Rank.HIGH)
    }

    private fun createMatchesForRank(size: Int, rank: Rank) {
        repeat(size) {
            val match = Match(
                rank = rank
            )
            logger.info { "Match created for rank: $rank" }
            matchRepository.save(match)
        }
    }
}
