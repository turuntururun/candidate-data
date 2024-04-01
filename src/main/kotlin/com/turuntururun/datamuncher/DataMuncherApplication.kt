package com.turuntururun.datamuncher

import com.turuntururun.datamuncher.data.CandidateDTO
import com.turuntururun.datamuncher.data.CandidateRepo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class DataMuncherApplication

fun main(args: Array<String>) {
    runApplication<DataMuncherApplication>(*args)
}

@RestController
class RestController(
    val candidateRepo: CandidateRepo
){

    @GetMapping("/candidates/{state}/{district}")
    fun hello(@PathVariable state: String?, @PathVariable district: String?): Collection<CandidateDTO> {
        // todo filter fields shown to the client
        return candidateRepo.findAllElectableByStateAndDistrict(state, district)
    }

}
