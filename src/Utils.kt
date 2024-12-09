import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Given a list of string with two elements separated by whitespace (such as "4     3"), return a pair of each list.
 */
fun convertListOfStringToPairOfLists(input: List<String>): Pair<List<Int>, List<Int>> = input.map { it ->
        it.split(" ")
            .filter { it != "" }
            .map { it.toInt() }
    }.map {
        val(x, y) = it
        return@map Pair(x, y)
    }.unzip()

/**
 * Get the input as a grid.
 */
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

fun goSouth(x: Int, y: Int): Pair<Int, Int> = Pair(x + 1, y)
fun goNorth(x: Int, y: Int): Pair<Int, Int> = Pair(x - 1, y)
fun goEast(x: Int, y: Int): Pair<Int, Int> = Pair(x, y + 1)
fun goWest(x: Int, y: Int): Pair<Int, Int> = Pair(x, y - 1)
fun goNortheast(x: Int, y: Int): Pair<Int, Int> = Pair(x - 1, y + 1)
fun goNorthwest(x: Int, y: Int): Pair<Int, Int> = Pair(x - 1, y - 1)
fun goSoutheast(x: Int, y: Int): Pair<Int, Int> = Pair(x + 1, y + 1)
fun goSouthwest(x: Int, y: Int): Pair<Int, Int> = Pair(x + 1, y - 1)

fun goSouthPoint(point: Pair<Int, Int>): Pair<Int, Int> = goSouth(point.first, point.second)
fun goNorthPoint(point: Pair<Int, Int>): Pair<Int, Int> = goNorth(point.first, point.second)
fun goEastPoint(point: Pair<Int, Int>): Pair<Int, Int> = goEast(point.first, point.second)
fun goWestPoint(point: Pair<Int, Int>): Pair<Int, Int> = goWest(point.first, point.second)

enum class Direction {
    NORTH, SOUTH, WEST, EAST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
}

fun indexIsInBounds(index: Pair<Int, Int>, grid: List<List<Any>>): Boolean =
    index.first >= 0 && index.first < grid.size && index.second >= 0 && index.second < grid.first().size


/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/** Get all indexes of a given element, e, in an Interable. **/
fun <E> Iterable<E>.indexesOf(e: E)
= mapIndexedNotNull{ index, elem -> index.takeIf{ elem == e } }

/** Get all indexes that do not match a given element, e, in an Interable. **/
fun <E> Iterable<E>.indexesOfNot(e: E)
        = mapIndexedNotNull{ index, elem -> index.takeIf{ elem != e } }

fun <E> Iterable<Set<E>>.nonEmptyIndexes()
        = mapIndexedNotNull{ index, elem -> index.takeIf{ elem.isNotEmpty() } }

fun <T> List<List<T>>.println() = forEach { it.forEach { print(it) }; print('\n') }
/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
