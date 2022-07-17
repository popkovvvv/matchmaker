package org.partymaker.matchmaker.entity.player

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface PlayerRepository : CrudRepository<Player, Long> {

    @Query(
        """
            select p
            from Player p
            where p.name = :name
        """
    )
    fun findPlayerByName(@Param("name") name: String): Player?

    @Query(
        """
            select *
            from players p
            where p.started_search_at is not null
             and cast(p.state->>'inGame' as boolean) = false
        """,
        nativeQuery = true
    )
    fun findSearchMatchPlayers(): List<Player>
}
