import java.util.LinkedList

fun main() {

    fun iterateStone(stone: Long): Pair<Long, Long?> {
        return when {
            stone == 0L -> Pair(1L, null)
            (stone.toString().length % 2 == 0) -> Pair(stone.toString().take(stone.toString().length / 2).toLong(), stone.toString().takeLast(stone.toString().length / 2).toLong())
            else -> Pair(stone * 2024, null)
        }
    }

    fun part1(input: List<String>, blinks: Int): Long {
        fun blink(stones: LinkedList<Long>): LinkedList<Long> {
            val getNext = stones.map { iterateStone(it) }
            val nextAsLongs: LinkedList<Long> = LinkedList<Long>()
            getNext.forEach {
                nextAsLongs.add(it.first)
                if (it.second != null) nextAsLongs.add(it.second!!)
            }
            return nextAsLongs
        }
        var linkedList: LinkedList<Long> = LinkedList()
        linkedList.addAll(input[0].split(" ").map { it.toLong() })
        for (i in 1..blinks) {
            linkedList = blink(linkedList)
        }
        return linkedList.size.toLong()
    }

    fun part2(input: List<String>): Long {

        val memo: MutableMap<Pair<Long, Int>, Long> = mutableMapOf()

        fun iterate(stone: Long?, iteration: Int): Long {
            if(stone == null) return 0
            if(iteration == 0) return 1
            if(memo.containsKey(Pair(stone, iteration))){
                return memo.getValue(Pair(stone, iteration))
            }
            val nextValues = iterateStone(stone)
            memo[(Pair(stone, iteration))] =
                iterate(nextValues.first, iteration - 1) + iterate(nextValues.second, iteration - 1)
            return memo.getValue(Pair(stone, iteration))
        }


        val inputAsLongs = input[0].split(" ").map { it.toLong() }
        return inputAsLongs.sumOf {
            iterate(it, 75)
        }
    }

    val easyInput = readInput("Day11_easy")
    //part1(easyInput, 20)
    //part2(easyInput)

    val testInput = readInput("Day11_test")
    check(part1(testInput, 25) == 55312L) { "part1 - testInput should return 36, returned ${part1(testInput, 25)}" }
    //check(part2(testInput) == 81L) { "part2 - testInput should return 34, returned ${part2(testInput)}" }

    // Read the input from the `src/Day01.txt` file.

    val input = readInput("Day11")
    part1(input, 25).println()
    part2(input).println()
}
