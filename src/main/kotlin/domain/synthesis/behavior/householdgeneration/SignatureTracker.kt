package domain.synthesis.behavior.householdgeneration.refinement

import domain.synthesis.Signature
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import it.unimi.dsi.fastutil.ints.IntSets
import kotlin.math.abs

class SignatureTracker(
    val signatures: List<Signature>,
    amountOfAttributes: Int
) {

    val indices = signatures.indices
    private val signatureIndexMapper = signatures.withIndex().associate { it.value to _root_ide_package_.domain.synthesis.behavior.householdgeneration.SignatureIndex(
        it.index
    )
    }
    val size = signatures.size
    private val inverseIndices = Array(amountOfAttributes) { IntOpenHashSet() }

    /**
     * The largest shift that can occur in a target region by receiving 1 signature.
     */
    val largestDelta = signatures.maxOf { it.values.sumOf { abs(it) } }

    val largestDifference = 2 * largestDelta

    val largestDeltaPartition = signatures.maxBy { it.values.sumOf { abs(it) } }
    init {
        require(amountOfAttributes == signatures.maxOf { it.keys.max() } + 1) {
            "The highest key is the last attribute that is referenced by the signatures."
        }
        signatures.withIndex().forEach { (i, sig) ->
            sig.keys.forEach { key ->
                inverseIndices[key].add(i)
            }
        }
    }
    fun highestAttributeForIndex(attrIdx: Int) = signatures.maxOf {
        it[attrIdx] ?: 0
    }

    operator fun get(index: Int): Signature {
        return signatures[index]
    }

    /**
     * Return the indices of all signatures that have a nonzero entry for the requested attribute index.
     */
    fun getByAttributeIndex(attrIdx: Int): IntSet {
        return IntSets.unmodifiable(inverseIndices[attrIdx])
    }

    fun getByAttribute(attrIdx: Int): List<Signature> {
        return getByAttributeIndex(attrIdx).map { signatures[it] }
    }

    fun findSignatureIndex(signature: Signature): domain.synthesis.behavior.householdgeneration.SignatureIndex {
        return signatureIndexMapper[signature] ?: throw NoSuchElementException("There is no signature $signature in $signatureIndexMapper")
    }

    fun mask(keep: BooleanArray): SignatureTracker {
        return TODO()
    }
}
