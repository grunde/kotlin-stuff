package com.dy.converter

typealias ItemTransformation = (Map<String, String>) -> Map<String, String>

interface ItemConverter {
    fun convert(item: Map<String, String>): Map<String, String>
}

object FindifyItemConverter : ItemConverter {
    val transformations = listOf<ItemTransformation>(this::renameFields).asSequence()
    override fun convert(item: Map<String, String>): Map<String, String> {
        return transformations.fold(item) {acc, transformation -> transformation(acc)}
    }

    fun renameFields(item: Map<String, String>): Map<String, String> {
        val renamedFields : Map<String, String> = hashMapOf(
                "sku" to "id",
                "name" to "title",
                "url" to "product_url"
        )
        return hashMapOf(
                "id" to item.getValue("sku"),
                "title" to item.getValue("name"),
                "description" to item.getValue("description"),
                "price" to item.getValue("price"),
                "image_url" to item.getValue("image_url")
        )
    }

}

fun main(args: Array<String>) {
    val foo = fun(x: Int) = x*x
    val bar = fun(x: Int) = x+x
    val list = arrayListOf(foo, bar).asSequence()
    val perelman = list.fold(3) {acc, next -> next(acc)}
    println(perelman)
}