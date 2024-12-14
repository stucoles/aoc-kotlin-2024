interface Button {
    val changeX: Int
    val changeY: Int
    val costToPress: Int
    fun pressButton(state: State): State
}

data class State(
    val x: Long,
    val y: Long,
    val cost: Long,
    val numberOfAPresses: Long,
    val numberOfBPresses: Long
)

fun main() {
    //    val easyInput = readInput("Day13")
//    //part1(easyInput, 20)
//    //part2(easyInput)
    data class ButtonA(override val changeX: Int, override val changeY: Int) : Button {
        override fun pressButton(state: State): State = State(
            state.x + changeX,
            state.y + changeY,
            state.cost + costToPress,
            state.numberOfAPresses + 1,
            state.numberOfBPresses
        )

        override val costToPress = 3
    }

    data class ButtonB(override val changeX: Int, override val changeY: Int) : Button {
        override fun pressButton(state: State): State = State(
            state.x + changeX,
            state.y + changeY,
            state.cost + costToPress,
            state.numberOfAPresses,
            state.numberOfBPresses + 1
        )

        override val costToPress = 1
    }

    data class Machine(val buttonA: ButtonA, val buttonB: ButtonB, val prizeLocation: Pair<Long, Long>)

    fun processInput(input: List<String>): Machine {
        val aData = input[0].split('+', ',')
        val bData = input[1].split('+', ',')
        val prize = input[2].split('=', ',')
        //println(prize)
        //println(aData)
        val buttonA = ButtonA(aData[1].toInt(), aData[3].toInt())
        val buttonB = ButtonB(bData[1].toInt(), bData[3].toInt())
        val prizeLocation = Pair(prize[1].toLong(), prize[3].toLong())
        return Machine(buttonA, buttonB, prizeLocation)
    }

    fun processInput2(input: List<String>): Machine {
        val aData = input[0].split('+', ',')
        val bData = input[1].split('+', ',')
        val prize = input[2].split('=', ',')
        //println(prize)
        //println(aData)
        val buttonA = ButtonA(aData[1].toInt(), aData[3].toInt())
        val buttonB = ButtonB(bData[1].toInt(), bData[3].toInt())
        val prizeLocation = Pair(prize[1].toLong() + 10000000000000, prize[3].toLong() + 10000000000000)
        return Machine(buttonA, buttonB, prizeLocation)
    }

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


    fun findLowestCost(machine: Machine): Long {
        val visitedStates = mutableMapOf<State, Boolean>()
        fun isPromising(state: State, currentLowestCost: Long): Boolean {
            if (visitedStates[state] == false) {
                return false
            } //println("seen: $state");return false}
            if (state.cost >= currentLowestCost) { //println("exceeded cost: $state");
                return false
            }
            if (state.numberOfAPresses > 100) {  //println("exceeded A presses: $state");
                return false
            }
            if (state.numberOfBPresses > 100) {  //println("exceeded B presses: $state");
                return false
            }
            if (state.x > machine.prizeLocation.first) {  //println("exceeded x: $state");
                return false
            }
            if (state.y > machine.prizeLocation.second) {  //println("exceeded y: $state");
                return false
            }
            return true
        }

        var currentLowestCost: Long = Long.MAX_VALUE
        fun checkPress(state: State): Boolean {
            if (isPromising(state, currentLowestCost)) {
                visitedStates[state] = true
                if (state.x == machine.prizeLocation.first && state.y == machine.prizeLocation.second) {
                    //println("found lowest cost: $state")
                    currentLowestCost = state.cost
                    return true
                } else {
                    val checkA = checkPress(machine.buttonA.pressButton(state))
                    val checkB = checkPress(machine.buttonB.pressButton(state))
                    visitedStates[state] = checkA || checkB
                    return checkA || checkB
                }
            } else {
                visitedStates[state] = false
                return false
            }
        }

        val initialState = State(0, 0, 0, 0, 0)
        checkPress(initialState)
        //println("Lowest cost: $currentLowestCost")
        if (currentLowestCost == Long.MAX_VALUE) {
            return 0
        }
        return currentLowestCost
    }

    /*
     * prize.x = a.x * pressA + b.x * pressB
     * prize.y = a.y * pressA + b.y * pressB
     *
     * a bunch of algebra later and you get
     * (prize.x/b.x - prize.y/b.y) * 1/(x.a/x.b - y.a/y.b) = pressA
     * which means that pressB can be achieved by plugging pressA into the above, but only if A is an integer.
     */


    fun findLowestCost2(machine: Machine): Long {
        //val pushesOfA = (machine.prizeLocation.first.toFloat()/machine.buttonB.changeX - machine.prizeLocation.second.toFloat()/machine.buttonB.changeY) / (machine.buttonA.changeX.toFloat()/machine.buttonB.changeX - machine.buttonA.changeY.toFloat()/machine.buttonB.changeY)
        //val pushesOfAInteger = (machine.prizeLocation.first/machine.buttonB.changeX - machine.prizeLocation.second/machine.buttonB.changeY) / (machine.buttonA.changeX/machine.buttonB.changeX - machine.buttonA.changeY/machine.buttonB.changeY)
        val pushesOfANumerator = (machine.prizeLocation.first * machine.buttonB.changeY.toLong() - machine.prizeLocation.second * machine.buttonB.changeX.toLong())
        val pushesOfADenominator = (machine.buttonA.changeX.toLong() * machine.buttonB.changeY.toLong() - machine.buttonA.changeY.toLong() * machine.buttonB.changeX.toLong())
        val pushesOfA = pushesOfANumerator / pushesOfADenominator
        if(pushesOfANumerator % pushesOfADenominator != 0L) {
            //println("not possible")
            return 0L
        }
        val pushesOfBNumerator = (machine.prizeLocation.first - (pushesOfA * machine.buttonA.changeX))
        if(pushesOfBNumerator % machine.buttonB.changeX.toLong() != 0L) {
            return 0L
        }
        val pushesOfB = pushesOfBNumerator / machine.buttonB.changeX

        //println("Pushes of a: $pushesOfA")
        return pushesOfA * 3 + pushesOfB * 1
    }

    fun part1(input: List<String>): Long {
        val machines = buildList {
            var currentIndex = 3
            while (currentIndex < input.size) {
                this.add(processInput(input.subList(currentIndex - 3, currentIndex)))
                currentIndex += 4
            }
        }
        //println(machines)

        val costs = machines.sumOf { machine -> findLowestCost(machine) }
        return costs
    }

    fun part2(input: List<String>): Long {
        val machines = buildList {
            var currentIndex = 3
            while (currentIndex < input.size) {
                this.add(processInput2(input.subList(currentIndex - 3, currentIndex)))
                currentIndex += 4
            }
        }

        val costs = machines.sumOf { machine -> findLowestCost2(machine) }
        return costs
    }

    val testInput = readInput("Day13_test")
    //part1(testInput)
    check(part1(testInput) == 480L) { "part1 - testInput should return 480, returned ${part1(testInput)}" }
    //check(part2(testInput) == 81L) { "part2 - testInput should return 34, returned ${part2(testInput)}" }

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}