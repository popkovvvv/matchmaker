package unit.org.partymaker.matchmaker.builder

import io.github.serpro69.kfaker.Faker
import org.joda.time.DateTime
import org.partymaker.matchmaker.common.calculateRank
import org.partymaker.matchmaker.entity.Rank
import org.partymaker.matchmaker.entity.player.Player
import org.partymaker.matchmaker.entity.player.Player.Companion.State

class PlayerBuilder(
    val id: Long? = 1,
    val name: String = faker.name.name(),
    val skill: Double = (20..100).random().toDouble(),
    val latency: Double = (20..300).random().toDouble(),
    var startedSearchAt: DateTime? = null,
    var state: State = State(),
    var rank: Rank = skill.calculateRank()
) : Builder<Player> {

    override fun build(): Player = Player(
        id, name, skill, latency, startedSearchAt, state, rank
    )

    companion object {

        val faker = Faker()
    }
}
