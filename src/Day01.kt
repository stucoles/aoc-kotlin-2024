import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val mapInput = convertListOfStringToPairOfLists(input)
        return mapInput.first.sorted().zip(mapInput.second.sorted()) {
            first, second -> abs(first - second)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val mapInput = convertListOfStringToPairOfLists(input)
        return (mapInput.first).map { rightItem ->
            mapInput.second.count { it == rightItem }
        }.zip(mapInput.first).sumOf { similarity ->
            similarity.first * similarity.second
        }
    }

    // Test if implementation meets criteria from the description, like:
    //check(part1(listOf("test_input")) == 11)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
