package org.partymaker.matchmaker.service.event

import mu.KotlinLogging
import org.partymaker.matchmaker.entity.match.Match
import org.springframework.stereotype.Service

@Service
class StartMatchCallEventService : CallEventService<Match> {

    private val logger = KotlinLogging.logger { }

    override fun call(event: Match) {
        logger.info {
            """
                matchId: ${event.id},
                players: ${event.players.map { it.name }},
                skill statistic: ${event.skillStatistics},
                latency statistic: ${event.latencyStatistic},
                duration statistic: ${event.timeStatistic},
            """.trimIndent()
        }
    }
}
