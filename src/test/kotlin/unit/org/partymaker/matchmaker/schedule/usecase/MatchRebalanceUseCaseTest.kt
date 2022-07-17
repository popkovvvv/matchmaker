package unit.org.partymaker.matchmaker.schedule.usecase

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.PlayerRepository
import org.partymaker.matchmaker.service.usecase.MatchRebalanceRequest
import org.partymaker.matchmaker.service.usecase.MatchRebalanceUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import unit.org.partymaker.matchmaker.builder.MatchBuilder
import unit.org.partymaker.matchmaker.builder.PlayerBuilder

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        MatchRebalanceUseCase::class
    ]
)
@TestPropertySource("classpath:application-common.properties")
class MatchRebalanceUseCaseTest(
    @Autowired private val matchRebalanceUseCase: MatchRebalanceUseCase
) {

    @MockkBean
    private lateinit var matchRepository: MatchRepository

    @MockkBean
    private lateinit var playerRepository: PlayerRepository

    @Test
    fun `basic test`(): Unit = runBlocking {
        every { playerRepository.save(player) } returns player
        every { matchRepository.delete(matchToRebalance) } returns Unit
        matchRebalanceUseCase.assemble(MatchRebalanceRequest(matchToRebalance))
    }

    companion object {

        val player = PlayerBuilder(
            state = Player.Companion.State(
                findGameAttempts = 0,
                inGame = false
            ),
            startedSearchAt = DateTime()
        ).build()

        val matchToRebalance = MatchBuilder(players = setOf(player)).build()
    }
}
