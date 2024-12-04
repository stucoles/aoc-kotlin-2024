fun main() {

    val mulRegex = """mul\(([0-9]+),([0-9]+)\)""".toRegex()
    fun part1(input: List<String>): Int {
        val results = mulRegex.findAll(input.joinToString(""))
        return results.map {
            it.groupValues[1].toInt() * it.groupValues[2].toInt()
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val inputAsString = input.joinToString("")
        val doAndDont = """don't|do""".toRegex().findAll(inputAsString).map {
            it.range.first
        }
        val parts = mutableListOf<String>()
        var start = 0

        // I feel like there should be a better way to do this
        for (index in doAndDont) {
            parts.add(inputAsString.substring(start, index))
            start = index
        }
        parts.add(inputAsString.substring(start))

        return parts.map {
            if(it.startsWith("""don't""")) {
                0
            } else {
                mulRegex.findAll(it).map {
                    result -> result.groupValues[1].toInt() * result.groupValues[2].toInt()
                }.sum()
            }
        }.sum()
    }

    // Test if implementation meets criteria from the description, like:

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161) { "part1 - testInput should return 161, returned something else" }
    val testInput2 = readInput("Day03_test2")
    check(part2(testInput2) == 48)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
