package com.turuntururun.datamuncher.data

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@Entity
@Table(name = "candidates")
data class CandidateDTO(
    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Int? = null,
    var party: String?,
    @JsonIgnore
    var position: String?,
    var state: String?,
    var district: String?,
    var constituency: Int?,
    var ticketNumber: Int?,
    var name: String?,
    var type: String?,
    var age: Int?,
    var sex: String?,
    var address: String?,
    var phone: String?,
    var email: String?,
    var website: String?,
    @Column(length = 450) var socials: String?,
    var education: String?,
    var educationStatus: String?,
    @Column(length = 3_000) var extraEducation: String?,
    @Column(length = 5_300) var story: String?,
    @Column(length = 5_000) var politics: String?,
    @Column(length = 5_050) var motivation: String?,
    @Column(length = 2_000) var proposal1: String?,
    @Column(length = 2_000) var proposal2: String?,
    @Column(length = 2_000) var proposal3: String?,
) {
    constructor(map: Map<String, String>) : this(
        party = map["PARTIDO_COALICION"]?.trim(),
        position = map["CARGO"]?.trim(),
        state = map["ENTIDAD"]?.trim(),
        district = map["DISTRITO FEDERAL"]?.trim(),
        constituency = map["CIRCUNSCRIPCION"]?.toIntOrNull(),
        ticketNumber = map["NUM_LISTA_O_FORMULA"]?.toDoubleOrNull()?.toInt(),
        name = map["NOMBRE_CANDIDATO"]?.trim(),
        type = map["TIPO_CANDIDATO"]?.trim(),
        age = map["EDAD"]?.toDoubleOrNull()?.toInt(),
        sex = map["SEXO"]?.trim(),
        address = map["DIRECCION_CASA_CAMPAÑA"]?.trim(),
        phone = map["TELEFONO"]?.trim(),
        email = map["CORREO_ELECTRONICO"]?.trim(),
        website = map["PAGINA_WEB"]?.trim(),
        socials = map["REDES"]?.trim(),
        education = map["ESCOLARIDAD"]?.trim(),
        educationStatus = map["ESTATUS_ESCOLARIDAD"]?.trim(),
        extraEducation =  map["CURSOS"]?.trim(),
        story = map["HISTORIA_PROFESIONAL"]?.trim(),
        politics = map["TRAYECTORIA_POLITICA"]?.trim(),
        motivation = map["MOTIVO_CARGO_PUBLICO"]?.trim(),
        proposal1 = map["PROPUESTA_1"]?.trim(),
        proposal2 = map["PROPUESTA_2"]?.trim(),
        proposal3 = map["PROPUESTA_GENERO"]?.trim()
    )

    constructor() : this(emptyMap())
}

@Entity
@Table
data class StateConstituency(
    @Id
    var state: String = "",
    var constituency: Int? = null,
)

interface StateConstituencyRepo : JpaRepository<StateConstituency, String>
interface CandidateRepo : JpaRepository<CandidateDTO, String> {

    @Query("""
        SELECT C FROM CandidateDTO C WHERE 
            (C.position = 'PRESIDENCIA DE LA REPÚBLICA') OR
            (C.position IN ('SENADURÍA FEDERAL MR', 'GUBERNATURA','JEFATURA DE GOBIERNO') AND C.state = :state) OR 
            (C.position = 'DIPUTACIÓN FEDERAL MR' AND C.state = :state AND C.district = :district)
        ORDER BY C.party, C.type
        """)
    fun findAllElectableByStateAndDistrict(state: String?, district: String?): List<CandidateDTO>

    @Query("select distinct C.state, C.district from CandidateDTO C")
    fun listPlaces(): List<Array<String>>


}
