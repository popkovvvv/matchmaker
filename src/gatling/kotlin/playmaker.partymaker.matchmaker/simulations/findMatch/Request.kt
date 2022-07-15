package playmaker.partymaker.matchmaker.simulations.findMatch

import com.google.gson.JsonObject
import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CoreDsl.StringBody
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import io.github.serpro69.kfaker.Faker

object Request {

    private val faker = Faker()

    val findMatch: ChainBuilder = exec { session ->
        val body = JsonObject()
        body.addProperty("name", "${faker.name.name()}  ${(25..250).random()}")
        body.addProperty("skill", (25..250).random())
        body.addProperty("latency", (40..350).random())
        val s = session.set("body", body)
        s
    }.exec(
        http("find match")
            .post("/users")
            .header("Content-Type", "application/json")
            .body(StringBody("""#{body}"""))
            .check(status().`in`(200))
    )
}
