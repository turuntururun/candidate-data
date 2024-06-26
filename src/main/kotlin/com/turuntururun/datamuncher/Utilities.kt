package com.turuntururun.datamuncher

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.io.InputStream

fun readXlsx(path: String): Collection<Map<String, String>> {
    return readXls(FileInputStream(path))

}

fun readXls(fileInputStream: InputStream): Collection<Map<String, String>> {

    val headers = mutableListOf<String>()
    val builder = mutableListOf<Map<String, String>>()

    val workbook = HSSFWorkbook(fileInputStream)
    val sheet = workbook.getSheetAt(0)

    val iterator = sheet.iterator()

    while (iterator.hasNext()) {
        val row = iterator.next()

        val cellValues = row.iterator().toList { it.toString() }

        if (headers.isEmpty()) {
            headers.addAll(cellValues)
        } else {

            val dataMap = headers.mapIndexed { index, s -> Pair(s, cellValues[index]) }
                .associate { it }
            builder.add(dataMap)
        }


    }
    return builder

}

fun <A> Iterator<A>.toList(transformer: (A) -> String): List<String> {
    val list = mutableListOf<String>()
    while (this.hasNext()) {
        list.add(transformer(this.next()))
    }
    return list
}

