package domain.synthesis

/**
 * A signature represents a household in regard to a set of rules. An entry in the signature (k, v) represents that this
 * particular signature will return the value v for the k-th rule.
 *
 * Example the 3rd Rule is [amount of people with driver licence] and sig = {3:2} that would mean that two members of
 * the household have a driver licence.
 *
 * Note that the primitive signature definition is using boxing. This does not cause issues if the amount of rules
 * is below 128 due to Java Language Specification (JLS), §5.1.7 — Boxing Conversion
 * https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html
 *
 * Effectively meaning that if the number of rules is lower than 128 that the standard definition of map is sufficient.
 *
 * If this limit is exceeded maybe some tricks should be employed to maintain performance.
 *  -Djava.lang.Integer.IntegerCache.high=10000
 *  Or a manaul cache of Integers...
 */
typealias Signature = Map<Int, Int>
