fun main() {

    fun startPoint(grid: List<List<Char>>): Pair<Int, Int> {
        val row = grid.indexOfFirst { it.contains('@') }
        val col = grid[row].indexOfFirst { it == '@' }
        return Pair(row, col)
    }


    fun part1(input: List<List<Char>>, moves: String): Int {

        val grid = input.map { it.toMutableList() }
        var point = startPoint(grid)
        grid[point.first][point.second] = '.'

        fun performMove(direction: Direction): Unit {
            val moveFunction = direction.getMove()
            var currentPoint: Pair<Int, Int> = moveFunction(point.first, point.second)
            if (grid[currentPoint.first][currentPoint.second] == 'O') {
                while (grid[currentPoint.first][currentPoint.second] == 'O') {
                    currentPoint = moveFunction(currentPoint.first, currentPoint.second)
                }
                val isWall = grid[currentPoint.first][currentPoint.second] == '#'
                if (isWall) {
                    return
                } else {
                    grid[currentPoint.first][currentPoint.second] = 'O'
                    point = moveFunction(point.first, point.second)
                    grid[point.first][point.second] = '.'
                }
            } else {
                if (grid[currentPoint.first][currentPoint.second] == '#') {
                    return
                } else {
                    point = currentPoint
                }
            }
        }

        for (move in moves) {
            when (move) {
                '^' -> performMove(Direction.NORTH)
                '>' -> performMove(Direction.EAST)
                '<' -> performMove(Direction.WEST)
                'v' -> performMove(Direction.SOUTH)
                else -> throw IllegalArgumentException("Unrecognised move: $move")
            }
        }

        val sumOfGPSCoordinates = grid.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, char -> if (char == 'O') 100 * rowIndex + colIndex else 0 }
        }.flatten().sum()
        return sumOfGPSCoordinates
    }

    fun doubleGrid(grid: List<List<Char>>): List<MutableList<Char>> {
        return grid.map {
            row -> row.map {
                col -> when (col) {
                    '.' -> ".."
                    '@' -> "@."
                    '#' -> "##"
                    'O' -> "[]"
                    else -> throw Exception("Something went boom")
                }.toList()
            }.flatten().toMutableList()
        }
    }

    fun part2(input: List<List<Char>>, moves: String): Int {
        val grid = input.map { it.toMutableList() }
        var point = startPoint(grid)
        grid[point.first][point.second] = '.'
        val indicesToSwap: MutableSet<Pair<Int, Int>> = emptySet<Pair<Int, Int>>().toMutableSet()

        /**
         * Returns a list of all the indices that will be impacted by a particular move.
         * @param baseOfMove the point that "@" is at when the move is invoked. This should be a "." on the grid.
         * @param direction the direction to move in
         */
        fun getDisturbedIndices(baseOfMove: Pair<Int, Int>, direction: Direction) {
            val move = direction.getMove()
            val firstPoint =  move(baseOfMove.first, baseOfMove.second)
            if (grid[firstPoint.first][firstPoint.second] == '#') { return }
            if (grid[firstPoint.first][firstPoint.second] == '.') { return }
            if (grid[firstPoint.first][firstPoint.second] == '[') {
                indicesToSwap.add(firstPoint)
                indicesToSwap.add(goEastPoint(firstPoint))
                getDisturbedIndices(firstPoint, direction)
                getDisturbedIndices(goEastPoint(firstPoint), direction)
            }
            if (grid[firstPoint.first][firstPoint.second] == ']') {
                indicesToSwap.add(firstPoint)
                indicesToSwap.add(goWestPoint(firstPoint))
                getDisturbedIndices(firstPoint, direction)
                getDisturbedIndices(goWestPoint(firstPoint), direction)
            }
        }


        fun pushBlocksUpOrDown(direction: Direction, moveFunction: (Int, Int) -> Pair<Int, Int>) {
            getDisturbedIndices(point, direction)
            if(indicesToSwap.isEmpty()) {
                val pointToMoveTo = moveFunction(point.first, point.second)
                if(grid[pointToMoveTo.first][pointToMoveTo.second] == '#') {
                    return
                } else if(grid[pointToMoveTo.first][pointToMoveTo.second] == '.') {
                    point = pointToMoveTo
                } else {
                    throw Exception("Somehow you reached line 120, and a point that is filled in is not being swapped")
                }
                return
            }
            val sortedIndices: List<Pair<Int, Int>> = if (direction == Direction.NORTH) {
                indicesToSwap.toList().sortedBy { it.first }
            } else {
                indicesToSwap.toList().sortedByDescending { it.first }
            }
            val swapIndices = sortedIndices.map {
                moveFunction(it.first, it.second)
            }
            val isBlocked = swapIndices.map { grid[it.first][it.second] == '#' }.any { it }
            if (!isBlocked) {
                sortedIndices.forEachIndexed { index, pair ->
                    val temp = grid[swapIndices[index].first][swapIndices[index].second]
                    grid[swapIndices[index].first][swapIndices[index].second] = grid[pair.first][pair.second]
                    grid[pair.first][pair.second] = temp
                }
                point = moveFunction(point.first, point.second)
            }
            indicesToSwap.clear()
        }

        fun pushBlocksSideToSide(moveFunction: (Int, Int) -> Pair<Int, Int>) {
            var currentPoint: Pair<Int, Int> = moveFunction(point.first, point.second)
            if (grid[currentPoint.first][currentPoint.second] == '[' || grid[currentPoint.first][currentPoint.second] == ']') {
                while (grid[currentPoint.first][currentPoint.second] == '[' || grid[currentPoint.first][currentPoint.second] == ']') {
                    currentPoint = moveFunction(currentPoint.first, currentPoint.second)
                }
                val isWall = grid[currentPoint.first][currentPoint.second] == '#'
                if (isWall) {
                    return
                } else {
                    grid[point.first][point.second] = '.'
                    val oppositeMoveFunction = if (moveFunction == ::goEast) ::goWest else ::goEast
                    val nextChar = mapOf('[' to ']', ']' to '[')
                    var currentChar = if (moveFunction == ::goEast) ']' else '['
                    while(currentPoint != point) {
                        grid[currentPoint.first][currentPoint.second] = currentChar
                        currentChar = nextChar[currentChar] ?: throw Exception("Bad char in the map")
                        currentPoint = oppositeMoveFunction(currentPoint.first, currentPoint.second)
                    }

                    point = moveFunction(point.first, point.second)
                    grid[point.first][point.second] = '.'
                }
            } else {
                if (grid[currentPoint.first][currentPoint.second] == '#') {
                    return
                } else {
                    point = currentPoint
                }
            }
        }

        fun performMove(direction: Direction): Unit {
            val moveFunction = direction.getMove()
            if(direction == Direction.SOUTH || direction == Direction.NORTH) {
                pushBlocksUpOrDown(direction, moveFunction)
            }
            else {
                pushBlocksSideToSide(moveFunction)
            }
        }

        for (move in moves) {
            when (move) {
                '^' -> performMove(Direction.NORTH)
                '>' -> performMove(Direction.EAST)
                '<' -> performMove(Direction.WEST)
                'v' -> performMove(Direction.SOUTH)
                else -> throw IllegalArgumentException("Unrecognised move: $move")
            }
            grid[point.first][point.second] = '.'
        }


        val sumOfGPSCoordinates = grid.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, char -> if (char == '[') 100 * rowIndex + colIndex else 0 }
        }.flatten().sum()
        return sumOfGPSCoordinates
    }

    fun formatTestInput(input: List<String>): Pair<List<List<Char>>, String> {
        val grid: MutableList<MutableList<Char>> = mutableListOf()
        val sb = StringBuilder()
        for (line in input) {
            if (line.isEmpty()) {
                continue
            }
            if (line.startsWith('#')) {
                grid.add(line.map { it }.toMutableList())
            } else sb.append(line)
        }

        return Pair(grid, sb.toString())
    }

//    val easyTestInput = readInput("Day15_easy")
//    val (easyTestMap, easyTestMoves) = formatTestInput(easyTestInput)
//    //check(part1(easyTestMap, easyTestMoves) == 304)
//    val easyTestGrid = doubleGrid(easyTestMap)
//    part2(easyTestGrid, easyTestMoves)
//
    val smallTestInput = readInput("Day15_smalltest")
    val (smallTestMap, smallTestMoves) = formatTestInput(smallTestInput)
    check(part1(smallTestMap, smallTestMoves) == 2028)
//    val smallTestGrid = doubleGrid(smallTestMap)
//    part2(smallTestGrid, smallTestMoves)
    val testInput = readInput("Day15_test")
    val (testMap, testMoves) = formatTestInput(testInput)
    check(part1(testMap, testMoves) == 10092)
    val testGrid = doubleGrid(testMap)
    check(part2(testGrid, testMoves) == 9021) { "Part 2 Test failed. Expected 9021." }

    val input = readInput("Day15")
    val (map, moves) = formatTestInput(input)
    part1(map, moves).println()
    val grid = doubleGrid(map)
    part2(grid, moves).println()


}