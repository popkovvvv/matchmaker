package org.partymaker.matchmaker.schedule

import mu.KotlinLogging
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PlayerLongerSearchSchedule(
    private val playerRepository: PlayerRepository,
    @Value("\${match.search.ttl.seconds}") private val ttlSeconds: Int,
) : Schedule {

    private val logger = KotlinLogging.logger { }

    @Scheduled(fixedRate = 5000)
    override fun run() {
        val players = playerRepository.findLongestSearchMatchPlayers(ttlSeconds)
        players.forEach { player ->
            player.state = player.state.copy(
                priority = true
            )
            playerRepository.save(player)
        }
        val playersNames = players.map { it.name }.joinToString()
        logger.info {
            "players with long search: $playersNames"
        }
    }
}
