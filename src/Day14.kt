fun main() {

    data class Robot(val xPosition: Int, val yPosition: Int, val xVelocity: Int, val yVelocity: Int) {}
    fun processInput(input: List<String>): List<Robot> {
        return input.map {
            val splitString = it.split('=', ',', ' ')
            Robot(splitString[1].toInt(), splitString[2].toInt(), splitString[4].toInt(), splitString[5].toInt())
        }
    }

    fun moveRobot(robot: Robot, iterations: Int, xDimension: Int, yDimension: Int): Robot {
        var xPosition = (robot.xPosition + iterations * robot.xVelocity) % xDimension
        var yPosition = (robot.yPosition + iterations * robot.yVelocity) % yDimension
        if (xPosition < 0) {
            xPosition += xDimension
        }
        if (yPosition < 0) {
            yPosition += yDimension
        }
        return Robot(xPosition, yPosition, robot.xVelocity, robot.yVelocity)
    }

    fun identifyQuadrant(robot: Robot, xDimension: Int, yDimension: Int): Int{
        val xMidpoint = (xDimension - 1)/2
        val yMidpoint = (yDimension - 1)/2
        fun inQuadrantOne(robot: Robot): Boolean = robot.xPosition < xMidpoint && robot.yPosition < yMidpoint
        fun inQuadrantTwo(robot: Robot): Boolean = robot.xPosition > xMidpoint && robot.yPosition < yMidpoint
        fun inQuadrantThree(robot: Robot): Boolean = robot.xPosition < xMidpoint && robot.yPosition > yMidpoint
        fun inQuadrantFour(robot: Robot): Boolean = robot.xPosition > xMidpoint && robot.yPosition > yMidpoint

        return when {
            inQuadrantOne(robot) -> 1
            inQuadrantTwo(robot) -> 2
            inQuadrantThree(robot) -> 3
            inQuadrantFour(robot) -> 4
            else -> 0
        }
    }

    fun getGrid(input: List<Robot>, xDimension: Int, yDimension: Int): List<List<String>> {
        val grid = mutableListOf<MutableList<Int>>()
        repeat(yDimension) {
            grid.add(mutableListOf())
            repeat(xDimension) {
                grid.last().add(0)
            }
        }
        input.forEach{ robot ->
            grid[robot.yPosition][robot.xPosition]++
        }

        return grid.map { it.map { amount -> if(amount > 0) "🤖" else "⬛️" }}
    }

    fun printGrid(grid: List<List<Any>>, iterations: Int) {
        println("####### ITERATION # $iterations ######")
        grid.println()
    }
    fun part1(input: List<Robot>, iterations: Int, xDimension: Int, yDimension: Int): Int {
        //println(input)
        var quadrantOne = 0
        var quadrantTwo = 0
        var quadrantThree = 0
        var quadrantFour = 0

        val finalPositions = input.map {
            moveRobot(it, iterations, xDimension, yDimension)
        }
        finalPositions.forEach {
            when (identifyQuadrant(it, xDimension, yDimension)) {
                1 -> quadrantOne++
                2 -> quadrantTwo++
                3 -> quadrantThree++
                4 -> quadrantFour++
                else -> {}
            }
        }

        return quadrantOne * quadrantTwo * quadrantThree * quadrantFour
    }

    fun maxSequentialItems(list: List<Any>): Int {
        var max = 0
        var currentStreak = 0
        for (i in list.indices) {
            if(i == 0 && list[i] == "🤖") {currentStreak = 1}
            else if(i == 0) { continue; }
            if (list[i] == "🤖") {
                currentStreak++
                if(currentStreak > max) {
                    max = currentStreak
                }
            } else {
                currentStreak = 0
            }
        }
        return max
    }

    fun part2(input: List<Robot>, xDimension: Int, yDimension: Int): Int {
        for(i in 0..10000) {
            val finalPositions = input.map {
                moveRobot(it, i, xDimension, yDimension)
            }
            val grid = getGrid(finalPositions, xDimension, yDimension)
            val possibleTree = grid.any {
                maxSequentialItems(it) > 8
            }
            if(possibleTree) {
                printGrid(grid, i)
            }
        }
        return 0
    }

    val testInputString = readInput("Day14_test")
    val testInput = processInput(testInputString)
    check(part1(testInput, 100,11,7) == 12) { "Expected 12 but got $testInput" }

    val inputString = readInput("Day14")
    val input = processInput(inputString)
    part1(input, 100, 101, 103).println()
    part2(input, 101, 103)

}