package org.partymaker.matchmaker.service.usecase

import org.partymaker.matchmaker.common.downGrade
import org.partymaker.matchmaker.common.upGrade
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.Player.Companion.State
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

data class FindMatchRequest(
    val player: Player,
    val matches: List<Match>
)

sealed class FindMatchResponse {

    data class Success(
        val match: Match
    ) : FindMatchResponse()

    object MatchNotFounded : FindMatchResponse()
}

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
                return FindMatchResponse.Success(match)
            }

            val matchFoundingStepTwo = matchFoundingStepTwo(player, matches)
            if (matchFoundingStepTwo != null) {
                val match = addPlayerToMatch(player, matchFoundingStepTwo)
                return FindMatchResponse.Success(match)
            }

            if (player.state.priority) {
                val matchFoundingWithPriority = matchFoundingWithPriority(player, matches)
                if (matchFoundingWithPriority != null) {
                    val match = addPlayerToMatch(player, matchFoundingWithPriority)
                    return FindMatchResponse.Success(match)
                }
            }

            FindMatchResponse.MatchNotFounded
        }
    }

    private fun matchFoundingStepOne(player: Player, matches: List<Match>): Match? {
        return matches.find {
            it.rank == player.rank ||
                (it.skillStatistics.min - rankBorderLine) <= player.skill &&
                (it.skillStatistics.max + rankBorderLine) >= player.skill
        }
    }

    private fun matchFoundingStepTwo(player: Player, matches: List<Match>): Match? {
        return matches.find {
            ((it.skillStatistics.min - rankBorderLine) - rankBorderLine) <= player.skill &&
                ((it.skillStatistics.max + rankBorderLine) + rankBorderLine) >= player.skill
        }
    }

    private fun matchFoundingWithPriority(player: Player, matches: List<Match>): Match? {
        return matches.find {
            player.rank.downGrade() == it.rank || player.rank.upGrade() == it.rank
        }
    }

    private fun addPlayerToMatch(player: Player, match: Match): Match {
        match.addPlayer(player)
        player.startedSearchAt = null
        player.state = State(inGame = true)
        playerRepository.save(player)
        return match
    }
}
