package org.partymaker.matchmaker.service.usecase

import org.joda.time.DateTime
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.Rank
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.Player
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import kotlin.math.ceil

data class FillingMatchesRequest(
    val players: List<Player>
)

sealed class FillingMatchesResponse {

    object Success : FillingMatchesResponse()
}

@Service
class FillingMatchesUseCase(
    private val matchRepository: MatchRepository,
    @Value("\${match.group.size}") private val matchGroupSize: Int,
) : UseCase<FillingMatchesRequest, FillingMatchesResponse> {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    override suspend fun assemble(request: FillingMatchesRequest): FillingMatchesResponse = io {
        with(request) {
            val lowSkilledPlayersSize = players.filter { it.rank == Rank.LOW }.size
            val middleSkilledPlayersSize = players.filter { it.rank == Rank.MIDDLE }.size
            val highSkilledPlayersSize = players.filter { it.rank == Rank.HIGH }.size

            val lowSkilledMatchesSize = ceil(lowSkilledPlayersSize.toDouble() / matchGroupSize.toDouble()).toInt()
            val middleSkilledMatchesSize = ceil(middleSkilledPlayersSize.toDouble() / matchGroupSize.toDouble()).toInt()
            val highSkilledMatchesSize = ceil(highSkilledPlayersSize.toDouble() / matchGroupSize.toDouble()).toInt()

            val matches = mapOf(
                Rank.LOW to lowSkilledMatchesSize,
                Rank.MIDDLE to middleSkilledMatchesSize,
                Rank.HIGH to highSkilledMatchesSize
            ).map { (rank, matchesSize) ->
                createMatchesForRank(matchesSize, rank)
            }.flatten()

            matchRepository.saveAll(matches)

            FillingMatchesResponse.Success
        }
    }

    private fun createMatchesForRank(size: Int, rank: Rank): List<Match> {
        val matches = mutableListOf<Match>()
        for (i in 1..size) {
            matches.add(Match(rank = rank, createdAt = DateTime.now()))
        }
        return matches
    }
}
