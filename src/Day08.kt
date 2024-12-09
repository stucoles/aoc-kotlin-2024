fun main() {

    fun getIndexesOfChar(char: Char, grid: List<List<Char>>): List<Pair<Int, Int>> =
        grid.flatMapIndexed { index, line -> line.indexesOf(char).map { Pair(index, it) } }

    fun getAntinodesFromIndexes(indexes: List<Pair<Int, Int>>, grid: List<List<Char>>): List<Pair<Int, Int>> {
        return indexes.flatMap { point1 ->
            val y =
                indexes.map { point2 -> point2.second + (point2.second - point1.second) }.filter { it != point1.second }
            val x = indexes.map { point2 -> point2.first + (point2.first - point1.first) }.filter { it != point1.first }
            x.zip(y).map { (x, y) -> Pair(x, y) }
        }.filter { indexIsInBounds(it, grid) }
    }

    fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)
    fun Pair<Int, Int>.minus(other: Pair<Int, Int>) = Pair(this.first - other.first, this.second - other.second)

    fun getHarmonicAntinodesFromIndexes(indexes: List<Pair<Int, Int>>, grid: List<List<Char>>): Set<Pair<Int, Int>> {
        val points = mutableSetOf<Pair<Int, Int>>()
        indexes.forEach { point1 ->
            val run = indexes.map { point2 -> point2.second - point1.second }
            val rise = indexes.map { point2 -> point2.first - point1.first }
            val slopes = rise.zip(run)
            val minimalSlopes = slopes.map { (rise, run) ->
                when {
                    run == 0 && rise == 0 -> Pair(0, 0)
                    run == 0 -> Pair(1, 0)
                    rise == 0 -> Pair(0, 1)
                    rise % run == 0 -> Pair(rise / run, 1)
                    run % rise == 0 -> Pair(1, run / rise)
                    else -> Pair(rise, run)
                }
            }
            minimalSlopes.forEach slopes@ { slope ->
                var newPoint = point1
                if (slope.first == 0 && slope.second == 0) {
                    points.add(newPoint)
                    return@slopes
                }
                while (indexIsInBounds(newPoint, grid)) {
                    points.add(newPoint)
                    newPoint = newPoint.plus(slope)
                }
                newPoint = point1.minus(slope)
                while (indexIsInBounds(newPoint, grid)) {
                    points.add(newPoint)
                    newPoint = newPoint.minus(slope)
                }
            }
        }
        return points
    }


    fun part1(input: List<String>): Int {
        val grid = getInputAsGrid(input)
        val chars = input.joinToString("").filter { it != '.' }.filter { it != '#' }.toSet()
        val antinodes = chars.flatMap {
            getAntinodesFromIndexes(getIndexesOfChar(it, grid), grid)
        }.toSet()
        return antinodes.count()
    }


    fun part2(input: List<String>): Int {
        val grid = getInputAsGrid(input)
        val chars = input.joinToString("").filter { it != '.' }.filter { it != '#' }.toSet()
        val antinodes = chars.flatMap {
            val points = getHarmonicAntinodesFromIndexes(getIndexesOfChar(it, grid), grid)
            points
        }.toSet()
        return antinodes.count()
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14) { "part1 - testInput should return 14, returned ${part1(testInput)}" }
    check(part2(testInput) == 34) { "part2 - testInput should return 34, returned ${part2(testInput)}" }

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
