package com.dy.converter

import com.opencsv.CSVReader
import java.io.Closeable
import java.io.FileReader

interface CsvReader {
    fun getLinesWithHeaders(): Iterator<Map<String, String>>
}

data class CsvReaderImpl(val path: String) : CsvReader {
    override fun getLinesWithHeaders(): CloseableIterator {
        val firstRow: Array<String> = getFirstRow()
        val rowsReader = com.opencsv.CSVReader(FileReader(path))
        val rowsReaderIterator = rowsReader.iterator()
        rowsReaderIterator.next()
        return CloseableIterator(firstRow, rowsReader, rowsReaderIterator)
    }

    private fun getFirstRow(): Array<String> {
        FileReader(path).use { fileReader ->
            return com.opencsv.CSVReader(fileReader).readNext()
        }
    }

    class CloseableIterator(val firstRow: Array<String>, val csvReader: CSVReader, val rowsReaderIterator: Iterator<Array<String>>) : Iterator<Map<String, String>>, Closeable {
        override fun close() {
            csvReader.close()
        }

        override fun hasNext(): Boolean = rowsReaderIterator.hasNext()

        override fun next(): Map<String, String> {
            val next = rowsReaderIterator.next()
            val tuples = firstRow zip next
            return tuples.map { it.first to it.second }.toMap()
        }
    }
}