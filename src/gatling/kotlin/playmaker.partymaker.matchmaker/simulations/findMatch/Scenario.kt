package playmaker.partymaker.matchmaker.simulations.findMatch

import io.gatling.javaapi.core.Choice
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.http.HttpDsl.http
import playmaker.partymaker.matchmaker.simulations.findMatch.Request.findMatch

object Scenario {

    val httpProtocol = http
        .header("Content-Type", "application/json")
        .baseUrl("http://localhost:8080")

    fun scn(): ScenarioBuilder = scenario("Find Match Scenario")
        .randomSwitch()
        .on(Choice.withWeight(100.0, findMatch))
}
