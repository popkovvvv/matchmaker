package org.partymaker.matchmaker.service.usecase

import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

data class FindMatchRequest(
    val player: Player,
    val matches: List<Match>
)

data class FindMatchResponse(
    val match: Match
)

@Service
class FindMatchUseCase(
    private val playerRepository: PlayerRepository,
    @Value("\${rank.border.line}") private val rankBorderLine: Int
) : UseCase<FindMatchRequest, FindMatchResponse> {

    override suspend fun assemble(request: FindMatchRequest): FindMatchResponse {
        return with(request) {
            val matchFoundingStepOne = matchFoundingStepOne(player, matches)
            if (matchFoundingStepOne != null) {
                val match = addPlayerToMatch(player, matchFoundingStepOne)
                FindMatchResponse(match)
            }

            val matchFoundingStepTwo = matchFoundingStepTwo(player, matches)
            if (matchFoundingStepTwo != null) {
                val match = addPlayerToMatch(player, matchFoundingStepTwo)
                FindMatchResponse(match)
            }

            val newMatch = Match(
                rank = player.rank
            )

            val match = addPlayerToMatch(player, newMatch)
            FindMatchResponse(match)
        }
    }

    private fun matchFoundingStepOne(player: Player, matches: List<Match>): Match? {
        return matches.find {
            it.rank == player.rank ||
                ((it.skillStatistics.min - rankBorderLine) <= player.skill && (it.skillStatistics.max + rankBorderLine) <= player.skill)
        }
    }

    private fun matchFoundingStepTwo(player: Player, matches: List<Match>): Match? {
        return matches.find {
            ((it.skillStatistics.min - rankBorderLine) - rankBorderLine) <= player.skill &&
                ((it.skillStatistics.max + rankBorderLine) + rankBorderLine) <= player.skill
        }
    }

    private fun addPlayerToMatch(player: Player, match: Match): Match {
        player.startedSearchAt = null
        player.state = Player.Companion.State(inGame = true)
        match.addPlayer(player)
        playerRepository.save(player)
        return match
    }
}
