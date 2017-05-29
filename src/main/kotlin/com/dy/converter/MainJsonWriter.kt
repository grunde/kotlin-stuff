package com.dy.converter

import com.dy.converter.converter.FindifyItemConverter
import com.dy.converter.reader.CsvReaderImpl
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.set
import com.google.gson.JsonObject
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

fun main(args: Array<String>) {
    val csvReaderImpl = CsvReaderImpl("/Users/etangrundstein/Downloads/1495876832057.csv")
    val printWriter = PrintWriter(OutputStreamWriter(BufferedOutputStream(FileOutputStream("/tmp/findify.json")), StandardCharsets.UTF_8))
    printWriter.use { writer ->
        csvReaderImpl.getLinesWithHeaders().use { reader ->
            reader.asSequence()
                    .map { FindifyItemConverter.convert(it) }
                    .map { toJson(it) }
                    .take(20000)
                    .forEach {
                        writer.println(it)
                    }
        }
    }
}

private fun toJson(it: Map<String, String>): JsonObject {
    val json = jsonObject(it.toList())
    json["price"] = it.getOrDefault("price", "0").toDouble()
    return json
}