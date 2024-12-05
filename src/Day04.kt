import java.lang.reflect.Array.set

fun main() {

    fun getInputAsGrid(input: List<String>) : List<List<Char>> {
        val returnList = mutableListOf<MutableList<Char>>()
        for (line in input) {
            returnList.add(mutableListOf())
            for (char in line) {
                returnList.last().add(char)
            }
        }
        return returnList
    }

    fun checkForXmas(input: List<List<Char>>, x: Int, y: Int, maxX: Int, maxY: Int, direction: (Int, Int) -> Pair<Int, Int>): Int {
        fun outOfBounds(newX: Int, newY: Int) = newX < 0 || newY < 0 || newX >= maxX || newY >= maxY
        if(input[x][y] != 'X') return 0
        var (nextX, nextY) = direction(x, y)
        //println("Next: $nextX, Next: $nextY, val: ${input[x][y]}")
        if(outOfBounds(nextX, nextY)) return 0;
        if(input[nextX][nextY] != 'M') {
            return 0
        }
        nextX = direction(nextX, nextY).first
        nextY = direction(nextX, nextY).second
        if(outOfBounds(nextX, nextY)) return 0
        if(input[nextX][nextY] != 'A') {
            return 0
        }
        nextX = direction(nextX, nextY).first
        nextY = direction(nextX, nextY).second
        if(outOfBounds(nextX, nextY)) return 0
        if(input[nextX][nextY] != 'S') {
            return 0
        }
        return 1
    }

    fun goSouth(x: Int, y: Int): Pair<Int, Int> = Pair(x + 1, y)
    fun goNorth(x: Int, y: Int): Pair<Int, Int> = Pair(x - 1, y)
    fun goEast(x: Int, y: Int): Pair<Int, Int> = Pair(x, y + 1)
    fun goWest(x: Int, y: Int): Pair<Int, Int> = Pair(x, y - 1)
    fun goNortheast(x: Int, y: Int): Pair<Int, Int> = Pair(x - 1, y + 1)
    fun goNorthwest(x: Int, y: Int): Pair<Int, Int> = Pair(x - 1, y - 1)
    fun goSoutheast(x: Int, y: Int): Pair<Int, Int> = Pair(x + 1, y + 1)
    fun goSouthwest(x: Int, y: Int): Pair<Int, Int> = Pair(x + 1, y - 1)

    fun part1(input: List<List<Char>>): Int {
        val maxX = input.size
        //println(input)
        val maxY = input[0].size
        var numxmas = 0
        //println("maxX: $maxX, maxY: $maxY")
        for(x in 0 until maxX) {
            for(y in 0 until maxY) {
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goSouth)
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goNorth)
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goEast)
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goWest)
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goNortheast)
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goNorthwest)
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goSoutheast)
                numxmas += checkForXmas(input, x = x, y = y, maxX, maxY, ::goSouthwest)
            }
        }
        //println(numxmas)
        return numxmas
    }

    fun getSquare(input: List<List<Char>>, x: Int, y: Int) : List<List<Char>> {
        if(x < 0 || x >= input.size) return listOf()
        if(y < 0 || y >= input[0].size) return listOf()
        val newList = mutableListOf<MutableList<Char>>()
        for (newx in x until x + 3) {
            if(newx >= input.size) return listOf()
            newList.add(mutableListOf())
            for (newy in y until y + 3) {
                if(newy >= input[0].size) return listOf()
                newList.last().add(input[newx][newy])
            }
        }
        return newList
    }

    fun checkForMas(input: List<List<Char>>, x: Int, y: Int, maxX: Int, maxY: Int, direction: (Int, Int) -> Pair<Int, Int>): Int {
        fun outOfBounds(newX: Int, newY: Int) = newX < 0 || newY < 0 || newX >= maxX || newY >= maxY
        if(input[x][y] != 'M') return 0
        var (nextX, nextY) = direction(x, y)
        if(outOfBounds(nextX, nextY)) return 0;
        if(input[nextX][nextY] != 'A') {
            return 0
        }
        nextX = direction(nextX, nextY).first
        nextY = direction(nextX, nextY).second
        if(outOfBounds(nextX, nextY)) return 0
        if(input[nextX][nextY] != 'S') {
            return 0
        }
        return 1
    }

    fun searchSquare(input: List<List<Char>>): Boolean {
        //if(input[0][0] != 'M' || input[0][0] != 'S') return false
        if(input[1][1] != 'A') return false
        val onethirtyfive = checkForMas(input, 0, 0, 3, 3, ::goSoutheast) == 1 || checkForMas(input, 2, 2, 3, 3, ::goNorthwest) == 1
        val fourtyfive = checkForMas(input, 2, 0, 3, 3, ::goNortheast) == 1 || checkForMas(input, 0, 2, 3, 3, ::goSouthwest) == 1
        //println("onethirtyfive: $onethirtyfive, fourtyfive: $fourtyfive")
        return onethirtyfive && fourtyfive
    }

    fun part2(input: List<List<Char>>): Int {
        val maxX = input.size
        val maxY = input[0].size
        //println("maxX: $maxX, maxY: $maxY")
        var numxmas = 0
        for(x in 0 until maxX) {
            for (y in 0 until maxY) {
                val square = getSquare(input, x, y)
                if (square.isEmpty()) continue
                if (searchSquare(square)) {numxmas += 1}
            }
        }
        println("numxmas: $numxmas")
        return numxmas
    }

    // Test if implementation meets criteria from the description, like:

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day04_test")
    val test_grid = getInputAsGrid(testInput)
    //println(test_grid)
    //check(part1(test_grid) == 18) { "part1 - testInput should return 18, returned something else" }
    //val testInput2 = readInput("Day03_test2")
    check(part2(test_grid) == 9)

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day04")
    val input_grid = getInputAsGrid(input)
    part1(input_grid).println()
    part2(input_grid).println()
}
