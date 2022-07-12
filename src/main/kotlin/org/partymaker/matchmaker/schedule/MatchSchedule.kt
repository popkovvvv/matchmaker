package org.partymaker.matchmaker.schedule

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.partymaker.matchmaker.service.event.CallEventService
import org.partymaker.matchmaker.service.usecase.FindMatchRequest
import org.partymaker.matchmaker.service.usecase.FindMatchResponse
import org.partymaker.matchmaker.service.usecase.UseCase
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MatchSchedule(
    private val playerRepository: PlayerRepository,
    private val matchRepository: MatchRepository,
    private val objectMapper: ObjectMapper,
    private val callEventService: CallEventService<Match>,
    private val findMatchUseCase: UseCase<FindMatchRequest, FindMatchResponse>,
    @Value("\${match.group.size}") private val matchGroupSize: Int,
) : Schedule {

    private val playerInSearch = mutableListOf<Player>()

    @Scheduled(fixedRate = 10000)
    override fun run() {
        runBlocking {
            val matchesList = io { matchRepository.findNotStartedMatches(matchGroupSize) }
            val players = io { playerRepository.findSearchMatchPlayers() }
            playerInSearch + players
            playerInSearch.forEach { player ->
                val findMatchResponse = findMatchUseCase.assemble(FindMatchRequest(player, matchesList))
                with(findMatchResponse) {
                    if (match.players.size == matchGroupSize) {
                        match.startedAt = DateTime.now()
                    }

                    io { matchRepository.save(match) }
                    callEventService.call(match)
                }
                playerInSearch.remove(player)
            }
        }
    }

    @KafkaListener(topics = ["players"], groupId = "match")
    fun listenGroupFoo(message: String) {
        val player: Player = objectMapper.readValue(message)
        playerInSearch.add(player)
    }
}
