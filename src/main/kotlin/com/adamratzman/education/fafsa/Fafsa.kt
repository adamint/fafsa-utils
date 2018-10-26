package com.adamratzman.education.fafsa

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.text.similarity.LevenshteinDistance
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("You need to enter one of the following options:\n" +
                listOf("lookup COLLEGE/UNIVERSITY NAME (optional city and state)").joinToString("\n"))
        return
    }
    if (args[0].equals("lookup", true)) {
        val lookup = args.asList().subList(1, args.size)
        if (lookup.isEmpty()) {
            println("You need to provide a school name to lookup")
            return
        }
        println("Most similar schools (name | code):")
        getSchools(lookup).forEach { println("${it.name} | ${it.code}") }
    } else main(arrayOf())
}

fun String.getResourceReader() = BufferedReader(InputStreamReader(School::class.java.classLoader.getResourceAsStream(this)))
data class School(val name: String, val code: String)

fun getSchools(lookupTemp: List<String>): List<School> {
    val lookup = lookupTemp.joinToString("")
    val parser = CSVParser("1819FedSchoolCodeList.csv".getResourceReader(), CSVFormat.EXCEL.withHeader())
    return parser.records.map { record ->
        val school = record.get("SchoolName").toLowerCase().replace("-", "").replace(" ", "")
        Triple(record.get("SchoolName").toLowerCase().split(" ").joinToString(" ") { it.capitalize() } + " " + record.get("City"), record.get("SchoolCode"), LevenshteinDistance.getDefaultInstance().apply(lookup.toLowerCase(), school.toLowerCase()))
    }.sortedBy { it.third }.map { School(it.first, it.second) }.take(5)
}