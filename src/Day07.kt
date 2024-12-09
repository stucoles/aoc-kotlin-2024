fun main() {

    fun preprocess(input: String): Pair<Long, List<Long>> {
        val splitLine = input.split(':')
        val result = splitLine.first().toLong()
        val values = splitLine.last().split(" ").filter { it.isNotBlank() }.map { it.toLong() }
        return Pair(result, values)
    }

    fun dividesEvenly(numerator: Long, denominator: Long): Boolean {
        return numerator % denominator == 0L
    }

    fun evaluateList(result: Long, inputs: List<Long>): Boolean {
        if (inputs.isEmpty()) return result == 1L
        val dividesEvenly = if (dividesEvenly(result, inputs.last())) evaluateList(
            result / inputs.last(),
            inputs.dropLast(1)
        ) else false
        val subtracts = evaluateList(result - inputs.last(), inputs.dropLast(1))
        return dividesEvenly || subtracts
    }

    fun part1(input: List<String>): Long {
        return input.map { preprocess(it) }.filter { evaluateList(it.first, it.second) }.map { it.first }.sum()
    }

    fun evaluateWithConcatenation(
        target: Long,
        sum: Long,
        remaining: List<Long>
    ): Boolean {
        val operations = listOf( { a: Long, b: Long -> a*b }, { a: Long, b: Long -> a + b }, { a: Long, b: Long -> "$a$b".toLong() }  )
        return when {
            remaining.isEmpty() -> target == sum
            sum > target -> false
            else -> operations.any { operation ->
                evaluateWithConcatenation(
                    target,
                    operation.invoke(sum, remaining[0]),
                    remaining.subList(1, remaining.size)
                )
            }
        }
    }

    fun part2(input: List<String>): Long {
        return input.map { preprocess(it) }.filter { evaluateWithConcatenation(it.first, it.second.first(), it.second.subList(1, it.second.size)) }.map { it.first }.sum()
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L) { "part1 - testInput should return 3749, returned ${part1(testInput)}" }
    check(part2(testInput) == 11387L) { "part2 - testInput should return 11387, returned ${part2(testInput)}" }
    println("validated test input")

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
