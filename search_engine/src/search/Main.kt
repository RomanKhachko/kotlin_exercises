package search

import search.SearchEngine.findRecords
import search.SearchEngine.getSearchStrategy
import java.io.File


object SearchEngine {
    var records = emptyList<String>()
        set(value) {
            field = value
            indexRecords()
        }
    private val index = mutableMapOf<String, MutableSet<Int>>()

    enum class SearchStrategy(val searchFunc: (Set<Int>, Set<Int>) -> Set<Int>, val lineIndexes: Set<Int> = IntRange(0, records.lastIndex).toSet()) {
        ANY({ s1, s2 -> s1.union(s2) }, setOf<Int>()),
        NONE({ s1, s2 -> s1 - s2 }),
        ALL({ s1, s2 -> s1.intersect(s2) });
    }

    fun getSearchStrategy(strategyName: String) = SearchStrategy.valueOf(strategyName)

    fun findRecords(searchKey: String, searchStrategy: SearchStrategy): Set<Int> {
        val keys = searchKey.toLowerCase().split(' ')
        var lineIndexes = searchStrategy.lineIndexes
        for (key in keys) {
            lineIndexes = searchStrategy.searchFunc(lineIndexes, index.getOrDefault(key, mutableSetOf()))
        }
        return lineIndexes
    }

    private fun indexRecords() {
        index.clear()
        for (i in 0..records.lastIndex) {
            for (recordComponent in records[i].split(" ")) {
                val key = recordComponent.toLowerCase()
                if (!index.containsKey(key)) index[key] = mutableSetOf()
                index[key]!!.add(i)
            }
        }
    }
}

fun main(args: Array<String>) {
    if (args[0] != "--data") return
    val path = args[1]
    SearchEngine.records = File(path).readLines()
    do {
        val result = processMenu()
    } while (result)
}

fun findPerson() {
    println("Select a matching strategy: ALL, ANY, NONE")
    val searchStrategyName = readLine()!!
    println("Enter data to search people:")
    val searchKey = readLine()!!
    val searchStrategy = getSearchStrategy(searchStrategyName)
    val result = findRecords(searchKey, searchStrategy)
    if (result.isEmpty()) println("No matching people found.") else result.forEach { println(SearchEngine.records[it]) }
}

fun exit() {
    println("Bye!")
}

fun printPeople() {
    for (record in SearchEngine.records) {
        println(record)
    }
}

fun processMenu(): Boolean {
    var operationResult = true
    println("=== Menu ===")
    when (readLine()!!.toInt()) {
        1 -> findPerson()
        2 -> printPeople()
        0 -> {
            exit()
            operationResult = false
        }
        else -> println("Incorrect option! Try again.")
    }
    return operationResult
}

