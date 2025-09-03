package utils

import edu.kit.ifv.mobitopp.actitoppNG.utils.indexOfSearch

/**
 * If the binary search reveals anything, then return the index number, but if not, then the preceding index is - 2 of
 * the result. Since the standard behavior of binary search returns the insertion point, the converted index of
 * [indexOfSearch] would return the higher element index, as this would be the normal insertion point in a list.
 *
 * To get the smaller index, we need to return the smaller number when the element is not present in the array.
 * Binary search indicates such a case with a negative sign. In the case that we found the element we return the index
 * as usual
 */
fun Int.smallerIndex(): Int {
    return if (this < 0) -this - 2 else this
}