package playmaker.partymaker.matchmaker.simulations.findMatch

import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.Simulation
import playmaker.partymaker.matchmaker.simulations.findMatch.Scenario.httpProtocol
import playmaker.partymaker.matchmaker.simulations.findMatch.Scenario.scn

class FindMatchSimulation : Simulation() {

    private val injectionStep = constantUsersPerSec(5.0).during(20)

    init {
        setUp(scn().injectOpen(injectionStep).protocols(httpProtocol))
            .maxDuration(20)
            .assertions(
                global().failedRequests().count().`is`(0),
                global().requestsPerSec().gte(20.0)
            )
    }

    override fun before() {
        println("Find match for players simulation start!")
    }

    override fun after() {
        println("Find match for players simulation is finished!")
    }
}
