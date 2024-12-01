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
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
