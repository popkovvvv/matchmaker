package org.partymaker.matchmaker.entity.player

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.joda.time.DateTime
import org.partymaker.matchmaker.entity.Rank
import javax.persistence.*

@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
@Entity
@Table(name = "players")
class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    val name: String,
    val skill: Double,
    val latency: Double,
    var startedSearchAt: DateTime? = null,
    @Enumerated(EnumType.STRING)
    @Type(type = "jsonb") @Column(columnDefinition = "jsonb") var state: State = State(),
    var rank: Rank
) {

    companion object {

        data class State(
            val inGame: Boolean = false,
            val priority: Boolean = false
        )
    }
}
