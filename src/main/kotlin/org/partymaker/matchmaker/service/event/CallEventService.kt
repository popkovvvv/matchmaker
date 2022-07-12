package org.partymaker.matchmaker.service.event

interface CallEventService<Event> {

    fun call(event: Event)
}
