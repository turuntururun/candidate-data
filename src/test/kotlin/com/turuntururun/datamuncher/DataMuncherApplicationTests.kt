package com.turuntururun.datamuncher

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DataMuncherApplicationTests {

    @Test
    fun contextLoads() {
    }

}

class ReadingTests {
    @ParameterizedTest
    @ValueSource(strings = [
        "src/test/resources/DMR-1711742031270.xlsx",
        "src/test/resources/baseDatosCandidatos.xls",
    ])
    fun readCsv(path: String) {
        println("Reading $path")
        val data = readXlsx(path)
        println(data)

    }

    @ParameterizedTest
    @ValueSource(strings = [
        "src/test/resources/baseDatosCandidatos.xls",
    ])
    fun exploreBiggerDB(path: String) {
        println("Reading $path")
        val data = readXlsx(path)
        // [PARTIDO_COALICION, CARGO, ENTIDAD, DISTRITO FEDERAL, CIRCUNSCRIPCION, MUNICIPIO, NUM_LISTA_O_FORMULA,
        // NOMBRE_CANDIDATO, TIPO_CANDIDATO, EDAD, SEXO, DIRECCION_CASA_CAMPAÑA, TELEFONO, CORREO_ELECTRONICO,
        // PAGINA_WEB, REDES, ESCOLARIDAD, ESTATUS_ESCOLARIDAD, CURSOS, HISTORIA_PROFESIONAL, TRAYECTORIA_POLITICA,
        // MOTIVO_CARGO_PUBLICO, PROPUESTA_1, PROPUESTA_2, PROPUESTA_GENERO]
        println(data.first().keys)

        data.filter { it["Entidad"] == "CIUDAD DE MEXICO" }
            .sortedBy { it["CARGO"] + it["PARTIDO_COALICION"] }
            .take(10)
            .forEach { println(it["NOMBRE_CANDIDATO"]) }
    }

}

