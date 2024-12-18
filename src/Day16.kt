import java.util.Objects
import kotlin.collections.ArrayDeque
import kotlin.time.measureTime

fun main() {

    fun findPosition(char: Char, input: List<List<Char>>): Pair<Int, Int> {
        val row = input.indexOfFirst { it.contains(char) }
        val col = input[row].indexOfFirst { it == char }
        return Pair(row, col)
    }

    fun fillDeadEnds(grid: List<List<Char>>): List<List<Char>> {
        var finalGrid = grid
        fun isDeadEnd(point: Pair<Int, Int>): Boolean {
            if(finalGrid[point.first][point.second] != '.') return false
            val neighbors = listOf(goNorthPoint(point), goEastPoint(point), goWestPoint(point), goSouthPoint(point))
            return neighbors.count {
                finalGrid[it.first][it.second] == '#'
            } >= 3
        }
        fun containsDeadEnds(): Boolean {
            for((row, list) in finalGrid.withIndex()) {
                for((col, _) in list.withIndex()) {
                    if(isDeadEnd(row to col)) { return true }
                }
            }
            return false
        }
        while(containsDeadEnds()) {
            finalGrid = finalGrid.mapIndexed {
                rIndex, row ->
                List(row.size) { cIndex -> if(isDeadEnd(rIndex to cIndex)) '#' else finalGrid[rIndex][cIndex]
                }
            }
        }

        return finalGrid
    }

    fun part1(grid: List<List<Char>>) : Long {

        val startPoint = findPosition('S', grid)
        val endPoint = findPosition('E', grid)

        data class State(val position: Pair<Int, Int>, val direction: Direction, val score: Long, val rotations: Int, val path: List<Pair<Int, Int>>?) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is State) return false
                return this.position == other.position && direction == other.direction && score == other.score
            }

            override fun hashCode(): Int {
                return Objects.hash(position, direction, score)
            }
        }

        val startState = State(startPoint, Direction.EAST, 0, 0, listOf(startPoint))
        val visitedStates = mutableMapOf<State, Boolean>(startState to true)
        val visitedPositions = mutableMapOf<Pair<Pair<Int, Int>, Direction>, Long>(Pair(startState.position, Direction.EAST) to Long.MAX_VALUE)
        val paths = mutableListOf<List<Pair<Int, Int>>>()
        var currentLowestScore = Long.MAX_VALUE

        /*
        Algorithm checknode(Node v, Best currBest)
            Node u
            if (promising(v, currBest))
                if (solution(v)) then
                    update(currBest) else
                for each child u of v
                    checknode(u, currBest)
                    return currBest
        */

        fun isPromising(state: State): Boolean {
            if (visitedStates[state] == false) return false
            if (visitedPositions.getOrDefault(Pair(state.position, state.direction), Long.MAX_VALUE) < state.score) return false
            if (state.score > currentLowestScore) return false
            if (grid[state.position.first][state.position.second] == '#')  {
                visitedPositions.put(Pair(state.position, Direction.EAST), -1)
                visitedPositions.put(Pair(state.position, Direction.WEST), -1)
                visitedPositions.put(Pair(state.position, Direction.NORTH), -1)
                visitedPositions.put(Pair(state.position, Direction.SOUTH), -1)
                return false
            }
            return true
        }

        fun calculateScore(startState: State, keepTrack: Boolean = false): Long {
            val stack = ArrayDeque<State>()
            stack.addLast(startState)

            while (stack.isNotEmpty()) {
                val state = stack.removeLast()
                //println("state: ${state.position}, ${state.score}, $currentLowestScore")
                if (state.rotations >= 3) continue
                if (!isPromising(state)) {
                    //println("not promising: ${state.position} facing ${state.direction}")
                    if (visitedPositions.getOrDefault(Pair(state.position, state.direction), Long.MAX_VALUE) > state.score) {
                        visitedPositions.put(Pair(state.position, state.direction), state.score)
                    }
                    visitedStates[state] = false
                    continue
                }
               // println("Promising: ${state.position} facing ${state.direction}")
                visitedStates[state] = true

                if(visitedPositions.getOrDefault(Pair(state.position, state.direction), Long.MAX_VALUE) > state.score) {
                    visitedPositions[Pair(state.position, state.direction)] = state.score
                }
                if (state.position == endPoint && state.score <= currentLowestScore) {
                    //println("found the end, new score ${state.score}")
                    state.path?.let { paths.add(it) }
                    currentLowestScore = state.score
                    continue
                }


                val forwardPosition = state.direction.getMove().invoke(state.position.first, state.position.second)
                stack.addLast(State(
                    forwardPosition,
                    state.direction,
                    state.score + 1,
                    0,
                    if(keepTrack) state.path?.plus(forwardPosition) else null
                ))
                stack.addLast(State(
                    state.position,
                    state.direction.rotateClockwise(),
                    state.score + 1000,
                    state.rotations + 1,
                    if(keepTrack) state.path else null
                ))
                stack.addLast(State(
                    state.position,
                    state.direction.rotateCounterClockwise(),
                    state.score + 1000,
                    state.rotations + 1,
                    if(keepTrack) state.path else null
                ))
            }

            return currentLowestScore
        }

        //calculateScore(startState, 0)
        val firstTimeTaken = measureTime {
            calculateScore(startState)
        }
        paths.clear()
        visitedPositions.clear()
        visitedStates.clear()
        println("Part1: $currentLowestScore")
        println("First run took $firstTimeTaken")
        println("########STARTING OVER########")
        val secondTimeTaken = measureTime {
            calculateScore(startState, true)
        }
        val seatsInPath = paths.flatten().toSet()
        println("Part2: ${seatsInPath.size}")
        println("Second run took $secondTimeTaken")
        return currentLowestScore
    }

    val testInput1 = fillDeadEnds(getInputAsGrid(readInput("Day16_test1")))
    val testInput2 = fillDeadEnds(getInputAsGrid(readInput("Day16_test2")))
    check(part1(testInput1) == 7036L) { "Test 1 failed" }
    check(part1(testInput2) == 11048L) { "Test 2 failed" }

    val input = fillDeadEnds(getInputAsGrid(readInput("Day16")))
    part1(input)
}