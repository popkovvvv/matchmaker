package org.partymaker.matchmaker.schedule

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.joda.time.DateTime
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.match.Match
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.partymaker.matchmaker.service.event.CallEventService
import org.partymaker.matchmaker.service.usecase.FindMatchRequest
import org.partymaker.matchmaker.service.usecase.FindMatchResponse
import org.partymaker.matchmaker.service.usecase.UseCase
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FindMatchSchedule(
    private val playerRepository: PlayerRepository,
    private val matchRepository: MatchRepository,
    private val callEventService: CallEventService<Match>,
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper,
    private val findMatchUseCase: UseCase<FindMatchRequest, FindMatchResponse>,
    @Value("\${match.group.size}") private val matchGroupSize: Int,
) : Schedule {

    private val logger = KotlinLogging.logger { }

    @Scheduled(fixedRate = 5000)
    override fun run() {
        runBlocking {
            val matchesList = io { matchRepository.findNotStartedMatches(matchGroupSize) }
            val players = io { playerRepository.findSearchMatchPlayers() }.toMutableList()
            players.forEach { player ->
                when (val resp = findMatchUseCase.assemble(FindMatchRequest(player, matchesList))) {
                    is FindMatchResponse.Success -> {
                        val match = resp.match
                        if (match.players.size == matchGroupSize) {
                            match.startedAt = DateTime.now()
                        }
                        io { matchRepository.save(match) }

                        callEventService.call(match)
                        players.remove(player)
                    }
                    is FindMatchResponse.MatchNotFounded -> {
                        logger.info { "Return ${player.name} to search match queue" }
                        logger.info { "Send ${player.name} to create match queue" }
                        kafkaTemplate.send("fill_matches", objectMapper.writeValueAsString(player))
                    }
                }
            }
        }
    }
}
