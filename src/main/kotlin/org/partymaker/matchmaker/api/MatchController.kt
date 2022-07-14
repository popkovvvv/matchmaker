package org.partymaker.matchmaker.api

import org.partymaker.matchmaker.service.usecase.AddPlayerToMatchMakingRequest
import org.partymaker.matchmaker.service.usecase.AddPlayerToMatchMakingResponse
import org.partymaker.matchmaker.service.usecase.UseCase
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MatchController(
    private val startGameUseCase: UseCase<AddPlayerToMatchMakingRequest, AddPlayerToMatchMakingResponse>
) {

    @PostMapping("/users", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun startGame(
        @RequestBody request: AddPlayerToMatchMakingRequest
    ): ResponseEntity<*> {
        return when (val res = startGameUseCase.assemble(request)) {
            AddPlayerToMatchMakingResponse.Success -> {
                ResponseEntity.ok().build<Any>()
            }
            is AddPlayerToMatchMakingResponse.PlayerInGame -> {
                ResponseEntity(ApiError(message = res.message), HttpStatus.BAD_REQUEST)
            }
            is AddPlayerToMatchMakingResponse.PlayerAlreadySearch -> {
                ResponseEntity(ApiError(message = res.message), HttpStatus.BAD_REQUEST)
            }
        }
    }
}
