package org.partymaker.matchmaker.service.usecase

import org.joda.time.DateTime
import org.partymaker.matchmaker.common.calculateRank
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.partymaker.matchmaker.service.usecase.AddPlayerToMatchMakingResponse.*
import org.springframework.stereotype.Service

data class AddPlayerToMatchMakingRequest(
    val name: String,
    val skill: Double,
    val latency: Double
)

sealed class AddPlayerToMatchMakingResponse {

    object Success : AddPlayerToMatchMakingResponse()

    data class PlayerInGame(
        val message: String,
    ) : AddPlayerToMatchMakingResponse()

    data class PlayerAlreadySearch(
        val message: String,
    ) : AddPlayerToMatchMakingResponse()
}

@Service
class AddPlayerToMatchMakingUseCase(
    val playerRepository: PlayerRepository,
) : UseCase<AddPlayerToMatchMakingRequest, AddPlayerToMatchMakingResponse> {

    override suspend fun assemble(request: AddPlayerToMatchMakingRequest): AddPlayerToMatchMakingResponse = io {
        val player = playerRepository.findPlayerByName(request.name) ?: Player(
            name = request.name,
            skill = request.skill,
            latency = request.latency,
            rank = request.skill.calculateRank()
        )

        if (player.state.inGame) return@io PlayerInGame(
            "Player with name: ${player.name} already in game"
        )

        if (player.startedSearchAt != null) return@io PlayerAlreadySearch(
            message = "Player with name: ${player.name} already search game"
        )

        player.startedSearchAt = DateTime.now()
        playerRepository.save(player)

        Success
    }
}
