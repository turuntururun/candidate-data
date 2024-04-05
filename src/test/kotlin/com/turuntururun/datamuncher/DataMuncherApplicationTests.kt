package com.turuntururun.datamuncher

import com.turuntururun.datamuncher.data.CandidateDTO
import com.turuntururun.datamuncher.data.CandidateRepo
import com.turuntururun.datamuncher.data.StateConstituency
import com.turuntururun.datamuncher.data.StateConstituencyRepo
import io.restassured.RestAssured.given
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL


@SpringBootTest(webEnvironment = RANDOM_PORT)
class DataMuncherApplicationTests {


    @Autowired
    lateinit var candidateRepo: CandidateRepo
    lateinit var stateConstituencyRepo: StateConstituencyRepo

    @Test
    fun readData() {

        val electableCandidatesByPosition =
            candidateRepo.findAllElectableByStateAndDistrict("CIUDAD DE MEXICO", "11-VENUSTIANO CARRANZA")

        println("electableCandidatesByPosition.size: " + electableCandidatesByPosition.size)

        electableCandidatesByPosition.forEach {
            println(it)
        }

    }

    @Test
    fun testApiForCDMX(@LocalServerPort port: Int) {
        given().port(port)
            .get("/candidates/CIUDAD DE MEXICO/11-VENUSTIANO CARRANZA")
            .prettyPeek()
            .then()
            .statusCode(200)

            .body("'PRESIDENCIA DE LA REPÚBLICA'", Matchers.notNullValue())
            .body("'PRESIDENCIA DE LA REPÚBLICA'[0].name", Matchers.`is`("BERTHA XOCHITL GALVEZ RUIZ"))
            .body("'PRESIDENCIA DE LA REPÚBLICA'[1].name", Matchers.`is`("JORGE ALVAREZ MAYNEZ"))
            .body("'PRESIDENCIA DE LA REPÚBLICA'[2].name", Matchers.`is`("CLAUDIA SHEINBAUM PARDO"))

            .body("'JEFATURA DE GOBIERNO'", Matchers.notNullValue())
            .body("'SENADURÍA FEDERAL MR'", Matchers.notNullValue())
            .body("'DIPUTACIÓN FEDERAL MR'", Matchers.notNullValue())

    }

    @Test
    fun testApiForYucatan(@LocalServerPort port: Int) {
        given().port(port)
            .get("/candidates/YUCATAN/4-MERIDA")
            .prettyPeek()
            .then()
            .statusCode(200)

            .body("'PRESIDENCIA DE LA REPÚBLICA'", Matchers.notNullValue())
            .body("'PRESIDENCIA DE LA REPÚBLICA'[0].name", Matchers.`is`("BERTHA XOCHITL GALVEZ RUIZ"))
            .body("'PRESIDENCIA DE LA REPÚBLICA'[1].name", Matchers.`is`("JORGE ALVAREZ MAYNEZ"))
            .body("'PRESIDENCIA DE LA REPÚBLICA'[2].name", Matchers.`is`("CLAUDIA SHEINBAUM PARDO"))

            .body("'GUBERNATURA'", Matchers.notNullValue())
            .body("'SENADURÍA FEDERAL MR'", Matchers.notNullValue())
            .body("'DIPUTACIÓN FEDERAL MR'", Matchers.notNullValue())

    }

    @Test
    fun testPlacesEndpoint(@LocalServerPort port: Int) {
        given().port(port)
            .get("/places")
            .prettyPeek()
            .then()
            .statusCode(200)
    }


}

class ReadingTests {
    @ParameterizedTest
    @ValueSource(
        strings = [
            "C:\\Users\\turun\\Downloads\\reporte_bd_identidad (1).xls",
            "C:\\Users\\turun\\Downloads\\reporte_bd_curricular.xls",
            "src/test/resources/candidatos-cdmx.xls",
        ]
    )
    fun readCsv(path: String) {
        println("Reading $path")
        val data = readXlsx(path)
        println(data)

    }

}
