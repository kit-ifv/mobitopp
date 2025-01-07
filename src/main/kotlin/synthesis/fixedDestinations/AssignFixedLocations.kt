package synthesis.fixedDestinations

import domain.data.ZoneId
import synthesis.fixedDestinations.BiMap.Companion.toBiMap
import utils.csv.DefaultCsvParser
import java.nio.file.Path


data class CommuterInfo(val origin: CommunityNumber, val destination: CommunityNumber, val amount: Int)

fun readCommuters(file: Path): Sequence<CommuterInfo> {
    val parser = DefaultCsvParser { row ->
        CommuterInfo(
            row("origin") { CommunityNumber.parse(it) },
            row("destination") { CommunityNumber.parse(it) },
            row("commuters").toInt(),

            )
    }

    return parser.parse(file.toFile())
}

@JvmInline
value class ZoneNumber(private val int: Int) {

    fun toZoneId(): ZoneId {
        return ZoneId(int.toLong())
    }

    companion object {
        fun parse(string: String): ZoneNumber {
            return ZoneNumber(string.toInt())
        }
    }
}

@JvmInline
value class CommunityNumber(private val int: Int) {
    companion object {
        fun parse(string: String): CommunityNumber {
            return CommunityNumber(string.toInt())
        }
    }
}

fun readZoneToCommunity(file: Path): BiMap<ZoneId, CommunityNumber> {
    val parser = DefaultCsvParser { row ->
        Pair(
            row("partId") { ZoneNumber.parse(it).toZoneId() },
            row("regionId") { CommunityNumber.parse(it) },

            )
    }

    return parser.parse(file.toFile()).toMap().toBiMap()
}


class BiMap<K, V>(
    val forwardMap: MutableMap<K, V> = mutableMapOf(),
    val backwardMap: MutableMap<V, List<K>> = mutableMapOf()
) {

    operator fun contains(element: K): Boolean {
        return element in forwardMap
    }

    fun containsValue(element: V): Boolean {
        return element in backwardMap
    }

    companion object {
        fun <K, V> Map<K, V>.toBiMap(): BiMap<K, V> {
            val backwardMap =
                this.entries.groupBy { it.value }.map { entry -> entry.key to entry.value.map { it.key } }.toMap()
            return BiMap(this.toMutableMap(), backwardMap.toMutableMap())
        }
    }
}







