package org.partymaker.matchmaker.schedule

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.service.usecase.FillingMatchesRequest
import org.partymaker.matchmaker.service.usecase.FillingMatchesResponse
import org.partymaker.matchmaker.service.usecase.UseCase
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class FillingMatchesSchedule(
    private val objectMapper: ObjectMapper,
    private val fillingMatchesUseCase: UseCase<FillingMatchesRequest, FillingMatchesResponse>
) : Schedule {

    private val playerInSearch: MutableList<Player> = mutableListOf()

    @Scheduled(fixedRate = 5000)
    override fun run() {
        runBlocking {
            fillingMatchesUseCase.assemble(FillingMatchesRequest(playerInSearch))
            playerInSearch.clear()
        }
    }

    @KafkaListener(topics = ["fill_matches"], groupId = "matchmaker")
    fun listenGroupFoo(message: String) {
        val player: Player = objectMapper.readValue(message)
        playerInSearch.add(player)
    }
}
