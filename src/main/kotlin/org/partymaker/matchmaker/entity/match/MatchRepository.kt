package org.partymaker.matchmaker.entity.match

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface MatchRepository : CrudRepository<Match, Long> {

    @Query(
        """
            select m
            from Match m
            where m.startedAt is null
        """
    )
    fun findNotStartedMatches(): List<Match>

    @Query(
        """
            select *
            from matches m
            where m.created_at < (now() - (:ttl * interval '1 second'))
            and m.started_at is null
        """,
        nativeQuery = true
    )
    fun findMatchesOlderWhen(ttl: Int): List<Match>
}
