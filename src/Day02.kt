import kotlin.math.abs

fun main() {
    fun reportIsSafe(report: List<Int>): Boolean {
        if (report.sorted() == report || report.sorted().asReversed() == report) {
            report.windowed(2).forEach { window -> if(abs(window[0] - window[1]) !in 1..3) return false }
        } else {
            return false
        }
        return true
    }
    fun part1(input: List<String>): Int {
        val inputAsInts = input.map { it -> it.split(" ").map { it.toInt() } }
        return inputAsInts.map { reportIsSafe(it) }.count { it }
    }

    fun part2(input: List<String>): Int {
        fun isSafeWhenDampened(report : List<Int>): Boolean {
            if (reportIsSafe(report)) {
                return true
            }
            return List(report.size) { i -> reportIsSafe(report.filterIndexed { j, _ -> j != i }) }.any { it }
        }

        val inputAsInts = input.map { it -> it.split(" ").map { it.toInt() } }
        return inputAsInts.map { isSafeWhenDampened(it) }.count { it }
    }

    // Test if implementation meets criteria from the description, like:

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02_test")
    //check(part1(testInput) == 2) { "part1 - testInput should return 2, returned something else" }
    //check(part2(testInput) == 4)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
