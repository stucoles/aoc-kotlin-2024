fun main() {

    fun findCoordinateOfIndex(input: Int, grid: List<List<Any>>): Pair<Int, Int> {
        val row = input / grid.size
        val col = input % grid.first().size
        return Pair(row, col)
    }

    fun findStartPosition(input: List<List<Char>>): Pair<Int, Int> {
        val flatIndex = input.flatten().indexOf('^')
        return findCoordinateOfIndex(flatIndex, input)
    }

    fun getDirection(input: Direction) = when (input) {
        Direction.NORTH -> Direction.EAST
        Direction.EAST -> Direction.SOUTH
        Direction.SOUTH -> Direction.WEST
        Direction.WEST -> Direction.NORTH
    }

    val directionMap = mapOf(
        Direction.NORTH to '^',
        Direction.EAST to '>',
        Direction.SOUTH to 'V',
        Direction.WEST to '<'
    )

    /**
     * Send the guard through the grid, and return the index where the guard leaves the grid. If the guard gets
     * caught in an infinite loop, return (-1, -1)
     */
    fun patrol(grid: List<List<Char>>) : Pair<Pair<Int, Int>, List<List<Set<Char>>>> {
        val startIndex = findStartPosition(grid)
        var direction = Direction.NORTH
        var move = ::goNorth
        var index = startIndex
        val directionGrid = grid.map { it -> it.map { mutableSetOf<Char>() } }
        fun getNextIndex(grid: List<List<Char>>, currentIndex: Pair<Int, Int>): Pair<Int, Int> {
            var nextindex = move(currentIndex.first, currentIndex.second)
            while (indexIsInBounds(nextindex, grid) && grid[nextindex.first][nextindex.second] == '#') {
                direction = getDirection(direction)
                move = directionToFunction[direction] ?: throw Exception("Unknown direction")
                nextindex = move(index.first, index.second)
            }
            return nextindex
        }

        fun checkForLoop(index: Pair<Int, Int>, directionGrid:  List<List<Set<Char>>>): Boolean {
            if(!indexIsInBounds(index, grid)) return false
            return directionGrid[index.first][index.second].contains(directionMap[direction])
        }

        while (indexIsInBounds(index, grid)) {
            if (grid[index.first][index.second] == '.' || grid[index.first][index.second] == '^') {
                directionGrid[index.first][index.second].add(directionMap[direction] ?: throw Exception("Unknown direction"))
            }
            index = getNextIndex(grid, index)
            if(checkForLoop(index, directionGrid)) {
                //println("Loop found!")
                index = Pair(-1, -1)
                break
            }
        }
        return Pair(index, directionGrid)
    }

    fun part1(input: List<String>): Int {
        val grid = getInputAsGrid(input)
        val (_, directionGrid) = patrol(grid)
        return directionGrid.flatten().count { it.isNotEmpty() }
    }



    fun part2(input: List<String>): Int {
        val grid = getInputAsGrid(input)
        val (_, firstDirectionGrid) = patrol(grid)
        val startPosition = findStartPosition(grid)
        val visitedCoordinates = firstDirectionGrid.flatten().nonEmptyIndexes().map {
            findCoordinateOfIndex(it, grid)
        }.filter { it != startPosition }

        var loops = 0
        // Really janky way of cutting execution time in half
        visitedCoordinates.parallelStream().forEach() { coordinate ->
            val obstructedGrid = grid.map { it.toMutableList() }.toMutableList()
            obstructedGrid[coordinate.first][coordinate.second] = '#'
            val (exit, _) = patrol(obstructedGrid)
            if (exit == Pair(-1, -1)) {
                loops += 1
            }
        }
        return loops
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41) { "part1 - testInput should return 41, returned something else" }
    check(part2(testInput) == 6) { "part2 - testInput should return 6, returned something else" }

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
