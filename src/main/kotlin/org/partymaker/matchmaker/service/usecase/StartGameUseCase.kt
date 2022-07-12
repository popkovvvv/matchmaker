package org.partymaker.matchmaker.service.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import org.joda.time.DateTime
import org.partymaker.matchmaker.common.calculateRank
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

data class StartGameRequest(
    val name: String,
    val skill: Double,
    val latency: Double
)

sealed class StartGameResponse {

    object Success : StartGameResponse()

    data class PlayerInGame(
        val message: String,
    ) : StartGameResponse()

    data class PlayerAlreadySearch(
        val message: String,
    ) : StartGameResponse()
}

@Service
class StartGameUseCase(
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper,
    val playerRepository: PlayerRepository,
) : UseCase<StartGameRequest, StartGameResponse> {

    override suspend fun assemble(request: StartGameRequest): StartGameResponse = io {
        val player = playerRepository.findPlayerByName(request.name) ?: Player(
            name = request.name,
            skill = request.skill,
            latency = request.latency,
            state = Player.Companion.State(),
            rank = request.skill.calculateRank()
        )

        if (player.state.inGame) return@io StartGameResponse.PlayerInGame(
            "Player with name: ${player.name} already in game"
        )

        if (player.startedSearchAt != null) return@io StartGameResponse.PlayerAlreadySearch(
            message = "Player with name: ${player.name} already search game"
        )

        player.startedSearchAt = DateTime.now()

        playerRepository.save(player)

        kafkaTemplate.send("players", objectMapper.writeValueAsString(player))

        StartGameResponse.Success
    }
}
