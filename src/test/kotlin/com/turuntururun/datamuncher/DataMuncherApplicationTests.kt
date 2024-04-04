package com.turuntururun.datamuncher

import com.turuntururun.datamuncher.data.CandidateDTO
import com.turuntururun.datamuncher.data.CandidateRepo
import com.turuntururun.datamuncher.data.StateConstituency
import com.turuntururun.datamuncher.data.StateConstituencyRepo
import io.restassured.RestAssured.given
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
    fun testApi(@LocalServerPort port: Int) {
        given().port(port)
            .get("/candidates/CIUDAD DE MEXICO/11-VENUSTIANO CARRANZA")
            .prettyPeek()
            .then()
            .statusCode(200)
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
            "src/test/resources/DMR-1711742031270.xlsx",
            "src/test/resources/baseDatosCandidatos.xls",
        ]
    )
    fun readCsv(path: String) {
        println("Reading $path")
        val data = readXlsx(path)
        println(data)

    }

}
