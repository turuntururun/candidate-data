package com.turuntururun.datamuncher

import com.opencsv.CSVReader
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.io.InputStream

fun readXlsx(path: String): Collection<Map<String, String>> {
    return readXls(FileInputStream(path))

}

fun readCsv(inputStream: InputStream): Collection<Map<String, String>> {
    val headers = mutableListOf<String>()
    val builder = mutableListOf<Map<String, String>>()

    val list = CSVReader(inputStream.reader())
        .readAll()

    list.forEach {rowValues ->

        if (headers.isEmpty()) {
            headers.addAll(rowValues)
        } else {

            val dataMap = headers.mapIndexed { index, s -> Pair(s, rowValues[index]) }
                .associate { it }
            builder.add(dataMap)
        }
    }
    return builder
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

fun readXlsx(fileInputStream: InputStream): Collection<Map<String, String>> {

    val headers = mutableListOf<String>()
    val builder = mutableListOf<Map<String, String>>()

    val workbook = XSSFWorkbook(fileInputStream)
    val sheet = workbook.getSheetAt(0)

    val iterator = sheet.iterator()

    while (iterator.hasNext()) {
        val row = iterator.next()

        val cellValues = row.iterator().toList { it.toString() }

        if (headers.isEmpty()) {
            headers.addAll(cellValues)
        } else {
            val dataMap = headers.mapIndexed { index, s ->
                try {
                    val cell = row.getCell(index)
                    when(cell?.cellType){
                        CellType._NONE -> TODO()
                        CellType.NUMERIC -> Pair(s, cell.numericCellValue.toString())
                        CellType.STRING -> Pair(s, cell.stringCellValue)
                        CellType.FORMULA -> TODO()
                        CellType.BLANK -> TODO()
                        CellType.BOOLEAN -> TODO()
                        CellType.ERROR -> TODO()
                        null -> Pair(s, "")
                    }
                } catch (e: IndexOutOfBoundsException){
                    System.err.println("Error getting index $index for title $s")
                    Pair(s, "Error reading")
                }
            }
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

