package com.turuntururun.datamuncher

import com.turuntururun.datamuncher.data.CandidateDTO
import com.turuntururun.datamuncher.data.CandidateRepo
import com.turuntururun.datamuncher.data.StateConstituency
import com.turuntururun.datamuncher.data.StateConstituencyRepo
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class DataMuncherApplication

fun main(args: Array<String>) {
    runApplication<DataMuncherApplication>(*args)
}


@RestController
@CrossOrigin("https://turuntururun.com", "http://localhost:3000")
class RestController(
    val candidateRepo: CandidateRepo,
    val stateConstituencyRepo: StateConstituencyRepo,
    val counter: ApiUsageCounter
) {
    private val log = KotlinLogging.logger(this.javaClass.name)

    private val placesCache: Map<String, List<String>> by lazy {
        candidateRepo.listPlaces().groupBy(
            { it[0] },
            { it[1] },
        )
    }

    @PostConstruct
    fun loadData() {

        candidateRepo.saveAll(readXls(ClassPathResource("candidatos-ine.xls").inputStream)
            .map { CandidateDTO(it) })

        candidateRepo.saveAll(
            readXlsx(ClassPathResource("candidatos-yucatan.xlsx").inputStream)
                .map {
                    val candidateDTO = CandidateDTO(it)
                    candidateDTO.state = "YUCATAN"
                    if (candidateDTO.district?.isNotEmpty() == true) {
                        candidateDTO.regionalDistrict = candidateDTO.district?.split(":")?.get(1)
                    }
                    candidateDTO.socials = listOf("facebook", "Instagram", "Twitter", "Tiktok", "YouTube")
                        .map { s -> it[s] }
                        .filter { f -> f?.isNotEmpty() ?: false }
                        .joinToString(",")
                    candidateDTO
                }

        )

        candidateRepo.saveAll(
            readXlsx(ClassPathResource("candidatos-cdmx.xlsx").inputStream)
                .map {
                    val candidateDTO = CandidateDTO(it)
                    candidateDTO.state = "CIUDAD DE MEXICO"
                    candidateDTO.socials = listOf(
                        "Facebook", "X(Twitter)", "Youtube", "Instagram", "Tiktok", "Otra"
                    )
                        .map { s -> it[s] }
                        .filter { f -> f?.isNotEmpty() ?: false }
                        .joinToString(",")
                    candidateDTO.extraEducation = listOf(
                        "Formación académica 1",
                        "Formación académica 2",
                        "Formación académica 3",
                        "Formación académica 4",
                        "Formación académica 5",
                        "Formación académica 6",
                        "Formación académica 7",
                        "Formación académica 8",
                        "Formación académica 9",
                        "Formación académica 10"
                    )
                        .map { s -> it[s] }
                        .filter { f -> f?.isNotEmpty() ?: false }
                        .joinToString(",")
                    candidateDTO
                }

        )

        candidateRepo.saveAll(
            readCsv(ClassPathResource("manual-curation.csv").inputStream)
                .map { CandidateDTO(it) }
        )

        listOf(
            setOf(
                "BAJA CALIFORNIA", "BAJA CALIFORNIA SUR", "CHIHUAHUA", "DURANGO",
                "JALISCO", "NACIONAL", "NAYARIT", "SINALOA", "SONORA"
            ),
            setOf(
                "AGUASCALIENTES", "COAHUILA", "GUANAJUATO", "NUEVO LEON", "QUERETARO",
                "SAN LUIS POTOSI", "TAMAULIPAS", "ZACATECAS"
            ),
            setOf("CAMPECHE", "CHIAPAS", "OAXACA", "QUINTANA ROO", "TABASCO", "VERACRUZ", "YUCATAN"),
            setOf("CIUDAD DE MEXICO", "GUERRERO", "MORELOS", "PUEBLA", "TLAXCALA"),
            setOf("COLIMA", "HIDALGO", "MEXICO", "MICHOACAN")
        ).flatMapIndexed { index: Int, names: Set<String> ->
            names.map {
                StateConstituency(it, index + 1)
            }
        }
            .let { stateConstituencyRepo.saveAll(it) }
    }

    @GetMapping("/candidates/{state}/{district}")
    fun getCandidates(@PathVariable state: String?, @PathVariable district: String?): Map<String?, List<CandidateDTO>> {
        log.info { "Loading candidates for $state in $district" }
        counter.candidates += 1
        return candidateRepo.findAllElectableByStateAndDistrict(state, district)
            .groupBy { it.position }
    }

    @Deprecated(
        "Flawed implementation left provisionally",
        replaceWith = ReplaceWith("Git data from the build process")
    )
    @GetMapping("/data-version")
    fun getDataCountAsVersion(): Long {
        counter.dataVersion += 1
        return candidateRepo.count()
    }

    @GetMapping("/places")
    fun getPlaces(): Map<String, List<String>> {
        counter.places += 1
        return placesCache
    }

}

@Component
class ApiUsageCounter : InfoContributor {

    var candidates: Int = 0
    var dataVersion: Int = 0
    var places: Int = 0

    override fun contribute(builder: Info.Builder?) {
        builder?.withDetail(
            "usage", mapOf(
                Pair("candidates", candidates),
                Pair("dataVersion", dataVersion),
                Pair("places", places),
            )
        )
    }
}
