fun main() {

    fun parseInput(input: List<String>): Pair<MutableMap<Int, Set<Int>>, List<List<Int>>> {
        val followableMap = mutableMapOf<Int, Set<Int>>()
        val listOfUpdates = mutableListOf<List<Int>>()
        var readingRules = true
        for (line in input) {
            if (line.isBlank()) {
                readingRules = false
                continue
            }
            if (readingRules) {
                val splitline = line.split("|")
                if (followableMap[splitline[0].toInt()].isNullOrEmpty()) {
                    followableMap[splitline[0].toInt()] = setOf(splitline[1].toInt())
                } else {
                    followableMap[splitline[0].toInt()] =
                        followableMap[splitline[0].toInt()]!!.plus(splitline[1].toInt())
                }
                continue
            }
            listOfUpdates.add(line.split(",").map { it.toInt() })
        }
        return Pair(followableMap, listOfUpdates)
    }

    fun fixUpdate(update: List<Int>, rules: Map<Int, Set<Int>>): List<Int> {
        val sortingComparator = object : Comparator<Int> {
            override fun compare(int1: Int, int2: Int): Int {
                if (int1 == int2) return 0
                val canFollow = rules[int1]?.contains(int2) ?: false
                //println("$int1 can follow $int2: $canFollow")
                if (canFollow) {
                    return -1
                }
                return 1
            }
        }
        return update.sortedWith(sortingComparator)
    }

    fun isValidUpdate(update: List<Int>, rules: Map<Int, Set<Int>>): Boolean {
        return fixUpdate(update, rules) == update
    }

    fun part1(input: List<String>): Int {
        val (followableMap, listOfUpdates) = parseInput(input)
        val sum = listOfUpdates.sumOf {
            if (isValidUpdate(it, followableMap)) it.elementAt(it.lastIndex / 2)
            else 0
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val (followableMap, listOfUpdates) = parseInput(input)
        return listOfUpdates.filter { !isValidUpdate(it, followableMap) }
            .sumOf { fixUpdate(it, followableMap).elementAt(it.lastIndex / 2) }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143) { "part1 - testInput should return 143, returned something else" }
    check(part2(testInput) == 123)

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
