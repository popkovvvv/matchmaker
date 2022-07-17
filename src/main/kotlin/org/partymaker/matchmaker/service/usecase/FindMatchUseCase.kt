package org.partymaker.matchmaker.service.usecase

import org.joda.time.DateTime
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.match.Match.Companion.Condition
import org.partymaker.matchmaker.entity.match.Match.Companion.Conditions
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.partymaker.matchmaker.service.event.CallEventService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

data class FindMatchRequest(
    val player: Player,
    val matches: List<Match>
)

sealed class FindMatchResponse {

    object MatchNotFounded : FindMatchResponse()
}

@Service
class FindMatchUseCase(
    private val playerRepository: PlayerRepository,
    private val matchRepository: MatchRepository,
    private val callEventService: CallEventService<Match>,
    @Value("\${rank.border.line}") private val rankBorderLine: Int,
    @Value("\${latency.border.line}") private val latencyBorderLine: Int,
    @Value("\${match.group.size}") private val matchGroupSize: Int,
) : UseCase<FindMatchRequest, FindMatchResponse> {

    override suspend fun assemble(request: FindMatchRequest): FindMatchResponse {
        return with(request) {
            findMatch(player, matches)?.let {
                val updatedMatch = addPlayerToMatch(player, it)
                if (updatedMatch.players.size == matchGroupSize) {
                    updatedMatch.startedAt = DateTime.now()
                    callEventService.call(updatedMatch)
                }
                io { matchRepository.save(updatedMatch) }
            }
            if (player.state.findGameAttempts < 3) {
                player.state = player.state.copy(
                    findGameAttempts = ++player.state.findGameAttempts
                )
                playerRepository.save(player)
            }
            FindMatchResponse.MatchNotFounded
        }
    }

    private fun findMatch(player: Player, matches: List<Match>): Match? {
        return matches.filter { it.startedAt == null }.firstOrNull {
            val sameRank = it.rank == player.rank
            if (!player.state.priority) {
                val sameSkill = it.conditions?.let { conditions ->
                    conditions.skillCondition.min <= player.skill && conditions.skillCondition.max >= player.skill
                } ?: true
                val sameLatency = it.conditions?.let { conditions ->
                    conditions.latencyCondition.min <= player.latency && conditions.latencyCondition.min >= player.latency
                } ?: true
                (sameRank && sameSkill && sameLatency) || (sameSkill && sameLatency)
            } else {
                sameRank
            }
        }
    }

    private fun addPlayerToMatch(player: Player, match: Match): Match {
        if (match.canAddPlayer(matchGroupSize)) {
            match.addPlayer(player)
            if (match.conditions == null) {
                val skillMinConditionValue = player.skill - rankBorderLine
                val skillMaxConditionValue = player.skill + rankBorderLine
                val latencyMinConditionValue = player.latency - latencyBorderLine
                val latencyMaxConditionValue = player.latency + latencyBorderLine
                match.conditions = Conditions(
                    skillCondition = Condition(
                        min = if (skillMinConditionValue < 0) player.skill else skillMinConditionValue,
                        max = skillMaxConditionValue
                    ),
                    latencyCondition = Condition(
                        min = if (latencyMinConditionValue < 0) player.latency else latencyMinConditionValue,
                        max = latencyMaxConditionValue
                    )
                )
            }
            player.startedSearchAt = null
            player.state = player.state.copy(inGame = true)
            playerRepository.save(player)
        }

        return match
    }
}
