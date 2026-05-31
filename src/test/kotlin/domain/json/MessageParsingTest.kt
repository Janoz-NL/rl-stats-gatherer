package com.janoz.rl.statgatherer.domain.json

import io.vertx.core.json.Json
import org.junit.jupiter.api.Test

class MessageParsingTest {
    val json =
        "{\"Event\":\"UpdateState\",\"Data\":\"{\\\"MatchGuid\\\":\\\"4366BADC95F480E3\\\"," +
            "\\\"Players\\\":[{\\\"Name\\\":\\\"Stijn\\\",\\\"PrimaryId\\\":\\\"Steam|1e|0\\\"," +
            "\\\"Shortcut\\\":6,\\\"TeamNum\\\":1,\\\"Score\\\":182,\\\"Goals\\\":0,\\\"Shots\\\":1," +
            "\\\"Assists\\\":1,\\\"Saves\\\":1,\\\"Touches\\\":33,\\\"CarTouches\\\":142,\\\"Demos\\\":4," +
            "\\\"bHasCar\\\":true,\\\"Speed\\\":22.09876823425293,\\\"Boost\\\":57},{\\\"Name\\\":\\\"Twan\\\"," +
            "\\\"PrimaryId\\\":\\\"Steam|79b|0\\\",\\\"Shortcut\\\":5,\\\"TeamNum\\\":1,\\\"Score\\\":632," +
            "\\\"Goals\\\":3,\\\"Shots\\\":5,\\\"Assists\\\":0,\\\"Saves\\\":1,\\\"Touches\\\":38," +
            "\\\"CarTouches\\\":93,\\\"Demos\\\":3,\\\"bOnGround\\\":true,\\\"bHasCar\\\":true," +
            "\\\"Speed\\\":57.82514572143555,\\\"Boost\\\":78,\\\"bBoosting\\\":true}," +
            "{\\\"Name\\\":\\\"Isa\\\",\\\"PrimaryId\\\":\\\"Steam|7c5|0\\\",\\\"Shortcut\\\":7,\\\"TeamNum\\\":1," +
            "\\\"Score\\\":176,\\\"Goals\\\":0,\\\"Shots\\\":1,\\\"Assists\\\":0,\\\"Saves\\\":0,\\\"Touches\\\":31," +
            "\\\"CarTouches\\\":102,\\\"Demos\\\":2,\\\"bOnGround\\\":true,\\\"bHasCar\\\":true," +
            "\\\"Speed\\\":60.88383865356445,\\\"Boost\\\":100},{\\\"Name\\\":\\\"Jasper\\\"," +
            "\\\"PrimaryId\\\":\\\"Steam|5f8|0\\\",\\\"Shortcut\\\":1,\\\"TeamNum\\\":0,\\\"Score\\\":447," +
            "\\\"Goals\\\":1,\\\"Shots\\\":3,\\\"Assists\\\":0,\\\"Saves\\\":2,\\\"Touches\\\":54," +
            "\\\"CarTouches\\\":99,\\\"Demos\\\":1},{\\\"Name\\\":\\\"Yasmine\\\"," +
            "\\\"PrimaryId\\\":\\\"Epic|655|0\\\",\\\"Shortcut\\\":2,\\\"TeamNum\\\":0,\\\"Score\\\":94," +
            "\\\"Goals\\\":0,\\\"Shots\\\":0,\\\"Assists\\\":0,\\\"Saves\\\":0,\\\"Touches\\\":24," +
            "\\\"CarTouches\\\":93,\\\"Demos\\\":3},{\\\"Name\\\":\\\"Xavier\\\"," +
            "\\\"PrimaryId\\\":\\\"Epic|dc|0\\\",\\\"Shortcut\\\":3,\\\"TeamNum\\\":0,\\\"Score\\\":192," +
            "\\\"Goals\\\":1,\\\"Shots\\\":3,\\\"Assists\\\":0,\\\"Saves\\\":0,\\\"Touches\\\":19," +
            "\\\"CarTouches\\\":125,\\\"Demos\\\":1}]," +
            "\\\"Game\\\":{\\\"Teams\\\":[{\\\"Name\\\":\\\"ALAMUT FEDAiLERi\\\",\\\"TeamNum\\\":0,\\\"Score\\\":2," +
            "\\\"ColorPrimary\\\":\\\"FF32B4\\\",\\\"ColorSecondary\\\":\\\"E5E5E5\\\"}," +
            "{\\\"Name\\\":\\\"JEMOEDER\\\",\\\"TeamNum\\\":1,\\\"Score\\\":3,\\\"ColorPrimary\\\":\\\"262626\\\"," +
            "\\\"ColorSecondary\\\":\\\"00B200\\\"}],\\\"TimeSeconds\\\":0,\\\"bOvertime\\\":true," +
            "\\\"Ball\\\":{\\\"Speed\\\":18.945615768432618,\\\"TeamNum\\\":0},\\\"bReplay\\\":true," +
            "\\\"bHasWinner\\\":true,\\\"Winner\\\":\\\"JEMOEDER\\\",\\\"Arena\\\":\\\"EuroStadium_Rainy_P\\\"," +
            "\\\"bHasTarget\\\":false}}\"}"

    @Test
    fun testUnmarshal() {
        println(json)
        val gs = Json.decodeValue(json, JsonRocketLeagueMessage::class.java)
        println(gs.data)
        val usd = Json.decodeValue(gs.data, JsonUpdateStateData::class.java)
        println(usd.game.arena)
    }
}
