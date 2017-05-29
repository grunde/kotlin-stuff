package com.dy.converter.reader

import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.Closeable
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


interface CsvReader {
    fun getLinesWithHeaders(): Iterator<Map<String, String>>
    fun getFirstItem(): Map<String, String>
}

data class CsvReaderImpl(val path: String) : CsvReader {
    override fun getFirstItem(): Map<String, String> {
        return getLinesWithHeaders().use { it.next() }
    }

    override fun getLinesWithHeaders(): CloseableIterator {
        val firstRow: Array<String> = getFirstRow()
        val rowsReader = getCSVReader(path)
        val rowsReaderIterator = rowsReader.iterator()
        rowsReaderIterator.next()
        return CloseableIterator(firstRow, rowsReader, rowsReaderIterator)
    }

    private fun getCSVReader(path: String): CSVReader {
        val rowsReader = CSVReader(BufferedReader(InputStreamReader(FileInputStream(path), StandardCharsets.UTF_8)))
        return rowsReader
    }

    private fun getFirstRow(): Array<String> {
        getCSVReader(path).use { reader ->
            return reader.readNext()
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