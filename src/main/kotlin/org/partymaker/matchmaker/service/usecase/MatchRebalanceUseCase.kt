package org.partymaker.matchmaker.service.usecase

import org.joda.time.DateTime
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

data class MatchRebalanceRequest(
    val match: Match
)

sealed class MatchRebalanceResponse {

    object Success : MatchRebalanceResponse()
}

@Service
class MatchRebalanceUseCase(
    val matchRepository: MatchRepository,
    val playerRepository: PlayerRepository
) : UseCase<MatchRebalanceRequest, MatchRebalanceResponse> {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    override suspend fun assemble(request: MatchRebalanceRequest): MatchRebalanceResponse {
        request.match.players.forEach { player ->
            player.state = player.state.copy(
                inGame = false,
                findGameAttempts = 0,
                priority = true
            )
            player.startedSearchAt = DateTime.now()
            playerRepository.save(player)
        }
        io { matchRepository.delete(request.match) }
        return MatchRebalanceResponse.Success
    }
}
