import java.util.Collections.swap

fun main() {

    fun Int.isEven() = this % 2 == 0
    fun <T> MutableList<T>.addCopies(value: T, n: Int) {
        val newList = buildList<T> {
            for (i in 1..n) {
                addLast(value)
            }
        }
        this.addAll(newList)
    }

    fun getDecompactedFilesystem(input: String): MutableList<Int> {
        val inputList = input.map { it.digitToInt() }
        val decompacted = buildList {
            inputList.forEachIndexed { index, value ->
                when (index.isEven()) {
                    true -> addCopies(index / 2, value)
                    false -> addCopies(-1, value)
                }
            }
        }.toMutableList()
        return decompacted
    }

    fun part1(decompacted: MutableList<Int>): Long {
        decompacted.forEachIndexed { index, value ->
            if (value == -1) {
                val indexOfLast = decompacted.indexOfLast { it >= 0 }
                if (indexOfLast > index) swap(decompacted, index, indexOfLast)
            }
        }
        return decompacted.filter { it != -1 }.mapIndexed { index, value -> index.toLong() * value.toLong() }.sum()
    }

    fun isContinuous(index: Int, list: List<Int>): Boolean {
        if (index == 0) return false
        return list[index - 1] == list[index] - 1
    }


    fun getFreeSpace(decompacted: MutableList<Int>): List<Pair<Int, Int>> {
        val freeSpace = decompacted.indexesOf(-1)
        val startIndexes = freeSpace.mapIndexed { index, _ ->
            isContinuous(index, freeSpace)
        }.indexesOf(false)
        val endIndexes = buildList<Int> {
            this.addAll(startIndexes.drop(1).map { it - 1 })
            this.addLast(startIndexes.last())
        }
        val emptySpaces = startIndexes.zip(endIndexes).map { (startIndex, endIndex) ->
            freeSpace[endIndex] - freeSpace[startIndex] + 1
        }.toMutableList()
        return emptySpaces.zip(startIndexes.map { freeSpace[it] })
    }

    fun part2(decompacted: MutableList<Int>): Long {
        var emptySpaces = getFreeSpace(decompacted)
        val filenames = decompacted.filter { it != -1 }.toSet().toList()
        val fileSizes = filenames.map {
            filename -> Pair(filename, decompacted.filter { it == filename }.count())
        }.asReversed()
        for(file in fileSizes) {
            val spaceToFill = emptySpaces.firstOrNull{ it.first >= file.second } ?: continue
            for(i in 0 until file.second) {
                decompacted[spaceToFill.second + i] = file.first
                decompacted.set(decompacted.lastIndexOf(file.first), -1)
                // this is god-awful inefficient but i want to go to bed and it works
                emptySpaces = getFreeSpace(decompacted)
            }

        }


        //println("Continuous free space: ${continuousFreeSpace}")
        return decompacted.mapIndexed { index, value -> if (value == -1) 0 else index.toLong() * value.toLong() }.sum()
    }

    val testInput = getDecompactedFilesystem(readInput("Day09_test").first())
    check(part1(testInput) == 1928L) { "part1 - testInput should return 1928, returned ${part1(testInput)}" }
    check(part2(testInput) == 2858L) { "part2 - testInput should return 2858, returns ${part2(testInput)}" }

    val input = getDecompactedFilesystem(readInput("Day09").first())
    part1(input).println()
    part2(input).println()
}
