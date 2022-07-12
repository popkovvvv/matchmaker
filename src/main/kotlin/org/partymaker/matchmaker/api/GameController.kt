package org.partymaker.matchmaker.api

import org.partymaker.matchmaker.service.usecase.StartGameRequest
import org.partymaker.matchmaker.service.usecase.StartGameResponse
import org.partymaker.matchmaker.service.usecase.UseCase
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(
    private val startGameUseCase: UseCase<StartGameRequest, StartGameResponse>
) {

    @PostMapping("/users", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun startGame(
        @RequestBody request: StartGameRequest
    ): ResponseEntity<*> {
        return when (val res = startGameUseCase.assemble(request)) {
            StartGameResponse.Success -> {
                ResponseEntity.ok().build<Any>()
            }
            is StartGameResponse.PlayerInGame -> {
                ResponseEntity(ApiError(message = res.message), HttpStatus.BAD_REQUEST)
            }
            is StartGameResponse.PlayerAlreadySearch -> {
                ResponseEntity(ApiError(message = res.message), HttpStatus.BAD_REQUEST)
            }
        }
    }
}
