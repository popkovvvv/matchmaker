package org.partymaker.matchmaker.service.usecase

interface UseCase<in Request, out Response> {

    suspend fun assemble(request: Request): Response
}
