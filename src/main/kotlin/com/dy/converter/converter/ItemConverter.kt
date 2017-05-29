package com.dy.converter.converter

import java.text.SimpleDateFormat
import java.util.*


typealias ItemTransformation = (Map<String, String>) -> Map<String, String>

interface ItemConverter {
    fun convert(item: Map<String, String>): Map<String, String>
}

object FindifyItemConverter : ItemConverter {
    val transformations = listOf<ItemTransformation>(
            this::transformFields,
            this::renameFields,
            this::copyFields
    ).asSequence()

    override fun convert(item: Map<String, String>): Map<String, String> {
        return transformations.fold(item) { acc, transformation -> transformation(acc) }
    }

    private fun transformFields(item: Map<String, String>): Map<String, String> {
        val fieldToTransformation: Map<String, (String) -> String> = hashMapOf(
                "in_stock" to { av -> if (av.toLowerCase() == "true") "in stock" else "out of stock" },
                "categories" to {categories -> categories.replace("|", " > ")},
                "created_at" to {_ -> timeNowIso() },
                "description" to {description -> description}
        )

        val forMerge = fieldToTransformation.entries.associate { it.key to it.value(item.getOrDefault(it.key, "")) }
        return item.plus(forMerge)
    }

    private fun copyFields(item: Map<String, String>): Map<String, String> {
        val copiedFields: Map<String, String> = hashMapOf(
                "image_url" to "thumbnail_url",
                "sku" to "id"
        )
        val forMerge: Map<String, String> = copiedFields.entries.associate { it.value to item.getValue(it.key) }
        return forMerge.plus(item)
    }

    private fun renameFields(item: Map<String, String>): Map<String, String> {
        val renamedFields: Map<String, String> = hashMapOf(
                "name" to "title",
                "url" to "product_url",
                "group_id" to "item_group_id",
                "categories" to "category",
                "in_stock" to "availability"
        )
        return item.mapKeys { renamedFields.getOrDefault(it.key, it.key) }
    }

    private fun timeNowIso(): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // Quoted "Z" to indicate UTC, no timezone offset
        df.timeZone = tz
        return df.format(Date())
    }
}