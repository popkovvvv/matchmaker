package org.partymaker.matchmaker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MatchmakerApplication

fun main(args: Array<String>) {
    runApplication<MatchmakerApplication>(*args)
}
