package org.partymaker.matchmaker.entity.match

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface MatchRepository : CrudRepository<Match, Long> {

    @Query(
        """
            select m
            from Match m
            where m.players.size < :groupSize and m.startedAt is null
        """
    )
    fun findNotStartedMatches(@Param("groupSize") groupSize: Int): List<Match>
}
