fun main() {

    data class Waypoint(val height: Int) {
        var hasPathToSummit: Boolean = height == 9
        var destinations: Set<Pair<Int, Int>> = setOf()
        val paths: MutableSet<List<Pair<Int, Int>>> = mutableSetOf()
    }

    fun hasPathToSummit(point: Pair<Int, Int>, terrain: List<List<Waypoint>>): Boolean {
        if (!indexIsInBounds(point, terrain)) return false

        with(terrain[point.first][point.second]) {
            if (this.height == 9) {
                terrain[point.first][point.second].destinations = destinations.plus(point)
            }
            if (this.hasPathToSummit) {
                return true
            }
            val neighbors = listOf(goNorthPoint(point), goEastPoint(point), goWestPoint(point), goSouthPoint(point))
            val neighborsHavePath = neighbors.map {
                when {
                    !indexIsInBounds(it, terrain) -> false
                    terrain[it.first][it.second].height != this.height + 1 -> false
                    else -> {
                        if (hasPathToSummit(it, terrain)) {
                            this.destinations = destinations.plus(terrain[it.first][it.second].destinations)
                            this.hasPathToSummit = true
                            true
                        } else false
                    }
                }
            }
            return neighborsHavePath.any()
        }
    }

    fun part1(input: List<String>): Int {
        val grid = getInputAsGrid(input).map { it.map { char -> Waypoint(char.digitToInt()) } }
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, point ->
                hasPathToSummit(Pair(x, y), grid)
            }
        }
        return grid.flatten().filter { it.height == 0 }.map { it.destinations.count() }.sum()
    }

    fun findPathsToSummit(path: List<Pair<Int, Int>>, startPoint: Pair<Int, Int>, terrain: List<List<Waypoint>>) {
        if (!indexIsInBounds(startPoint, terrain)) return
        with(terrain[startPoint.first][startPoint.second]) {
            if (this.height == 9) {
                paths.add(path.plus(startPoint))
            }
            val neighbors = listOf(
                goNorthPoint(startPoint),
                goEastPoint(startPoint),
                goWestPoint(startPoint),
                goSouthPoint(startPoint)
            )
            neighbors.forEach() {
                if(indexIsInBounds(it, terrain) && terrain[it.first][it.second].height == this.height + 1) {
                    findPathsToSummit(path.plus(startPoint), it, terrain)
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val grid = getInputAsGrid(input).map { it.map { char -> Waypoint(char.digitToInt()) } }
        grid.forEachIndexed { x, row ->
            row.forEachIndexed { y, point ->
                if (point.height == 0) {
                    findPathsToSummit(listOf(), startPoint = Pair(x, y), grid)
                }
            }
        }
        return grid.flatMap {
            row -> row.flatMap { value -> value.paths }
        }.size
    }

    val easyInput = readInput("Day10_easy")
    part1(easyInput)
    part2(easyInput)

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36) { "part1 - testInput should return 36, returned ${part1(testInput)}" }
    check(part2(testInput) == 81) { "part2 - testInput should return 34, returned ${part2(testInput)}" }

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
