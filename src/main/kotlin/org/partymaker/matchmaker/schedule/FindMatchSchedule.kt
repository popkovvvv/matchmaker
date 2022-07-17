package org.partymaker.matchmaker.schedule

import kotlinx.coroutines.runBlocking
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.partymaker.matchmaker.service.usecase.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FindMatchSchedule(
    private val playerRepository: PlayerRepository,
    private val matchRepository: MatchRepository,
    private val findMatchUseCase: UseCase<FindMatchRequest, FindMatchResponse>,
    private val fillingMatchesUseCase: UseCase<FillingMatchesRequest, FillingMatchesResponse>,
    @Value("\${match.group.size}") private val matchGroupSize: Int,
) : Schedule {

    @Scheduled(fixedRate = 8000)
    override fun run() {
        runBlocking {
            val matchesList = io { matchRepository.findNotStartedMatches() }
            val players = io { playerRepository.findSearchMatchPlayers() }
            val notFoundedMatchPlayers = mutableListOf<Player>()
            players.forEach { player ->
                when (findMatchUseCase.assemble(FindMatchRequest(player, matchesList))) {
                    is FindMatchResponse.MatchNotFounded -> {
                        if (player.state.findGameAttempts == 3) {
                            notFoundedMatchPlayers.add(player)
                        }
                    }
                }
            }
            fillingMatchesUseCase.assemble(FillingMatchesRequest(notFoundedMatchPlayers))
        }
    }
}
