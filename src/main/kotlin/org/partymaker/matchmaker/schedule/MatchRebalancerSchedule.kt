package org.partymaker.matchmaker.schedule

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.partymaker.matchmaker.common.io
import org.partymaker.matchmaker.entity.match.MatchRepository
import org.partymaker.matchmaker.service.usecase.MatchRebalanceRequest
import org.partymaker.matchmaker.service.usecase.MatchRebalanceResponse
import org.partymaker.matchmaker.service.usecase.UseCase
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MatchRebalancerSchedule(
    private val matchRepository: MatchRepository,
    private val matchRebalanceUseCase: UseCase<MatchRebalanceRequest, MatchRebalanceResponse>,
    @Value("\${match.filling.ttl}") private val matchFillingTtl: Int,
) : Schedule {

    private val logger = KotlinLogging.logger { }

    @Scheduled(fixedRate = 30000)
    override fun run() {
        runBlocking {
            val matchesList = io { matchRepository.findMatchesOlderWhen(matchFillingTtl) }
            matchesList.forEach {
                matchRebalanceUseCase.assemble(MatchRebalanceRequest(it))
            }
        }
    }
}
