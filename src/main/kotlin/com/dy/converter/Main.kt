package com.dy.converter

import com.dy.converter.converter.FindifyItemConverter
import com.dy.converter.reader.CsvReaderImpl
import com.opencsv.CSVWriter
import java.io.FileWriter

fun main(args: Array<String>) {
    val csvReaderImpl = CsvReaderImpl("/Users/etangrundstein/Downloads/1495876832057.csv")
    CSVWriter(FileWriter("/tmp/findify.csv")).use { writer ->
        csvReaderImpl.getLinesWithHeaders().use {
            reader ->
            val firstItem = FindifyItemConverter.convert(csvReaderImpl.getFirstItem())
            val sortedHeaders = firstItem.keys.sorted()
            writer.writeNext(sortedHeaders.toTypedArray())
            val recordsForPrint = reader.asSequence().map(FindifyItemConverter::convert).map { item ->
                sortedHeaders.map { item.getOrDefault(it, "") }
            }
            recordsForPrint.forEach { writer.writeNext(it.toTypedArray()) }
        }
    }
}