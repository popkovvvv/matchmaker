package unit.org.partymaker.matchmaker.schedule.usecase

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.partymaker.matchmaker.service.usecase.AddPlayerToMatchMakingRequest
import org.partymaker.matchmaker.service.usecase.AddPlayerToMatchMakingUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import unit.org.partymaker.matchmaker.builder.PlayerBuilder

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        AddPlayerToMatchMakingUseCase::class
    ]
)
@TestPropertySource("classpath:application-common.properties")
class AddPlayerToMatchMakingUseCaseTest(
    @Autowired private val addPlayerToMatchMakingUseCase: AddPlayerToMatchMakingUseCase
) {

    @MockkBean
    private lateinit var playerRepository: PlayerRepository

    @Test
    fun `basic test with existing player in db`(): Unit = runBlocking {
        every { playerRepository.findPlayerByName(player.name) } returns player
        every { playerRepository.save(player) } returns player
        addPlayerToMatchMakingUseCase.assemble(
            AddPlayerToMatchMakingRequest(
                name = player.name,
                skill = player.skill,
                latency = player.latency
            )
        )

        verify(exactly = 1) { playerRepository.findPlayerByName(player.name) }
        verify(exactly = 1) { playerRepository.save(player) }
    }

    @Test
    fun `basic test without player in db`(): Unit = runBlocking {
        every { playerRepository.findPlayerByName(player.name) } returns null
        every { playerRepository.save(any()) } returns player
        addPlayerToMatchMakingUseCase.assemble(
            AddPlayerToMatchMakingRequest(
                name = player.name,
                skill = player.skill,
                latency = player.latency
            )
        )

        verify(exactly = 1) { playerRepository.findPlayerByName(player.name) }
        verify(exactly = 1) { playerRepository.save(any()) }
    }

    @Test
    fun `basic test player already in game`(): Unit = runBlocking {
        every { playerRepository.findPlayerByName(playerInGame.name) } returns playerInGame
        addPlayerToMatchMakingUseCase.assemble(
            AddPlayerToMatchMakingRequest(
                name = playerInGame.name,
                skill = playerInGame.skill,
                latency = playerInGame.latency
            )
        )

        verify(exactly = 1) { playerRepository.findPlayerByName(playerInGame.name) }
        verify(exactly = 0) { playerRepository.save(any()) }
    }

    @Test
    fun `basic test player already search game`(): Unit = runBlocking {
        every { playerRepository.findPlayerByName(playerAlreadySearchGame.name) } returns playerAlreadySearchGame
        addPlayerToMatchMakingUseCase.assemble(
            AddPlayerToMatchMakingRequest(
                name = playerAlreadySearchGame.name,
                skill = playerAlreadySearchGame.skill,
                latency = playerAlreadySearchGame.latency
            )
        )

        verify(exactly = 1) { playerRepository.findPlayerByName(playerAlreadySearchGame.name) }
        verify(exactly = 0) { playerRepository.save(any()) }
    }

    companion object {

        private val player = PlayerBuilder().build()

        private val playerInGame = PlayerBuilder(
            state = Player.Companion.State(inGame = true)
        ).build()

        private val playerAlreadySearchGame = PlayerBuilder(
            startedSearchAt = DateTime()
        ).build()
    }
}
