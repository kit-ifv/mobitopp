package core.datastructure.matrix

import utils.units.AbsoluteTime

/**
 * A lookup structure for matrices.
 *
 * The index type [I] is generic: it could represent zones, locations,
 * or any other identifier that can be mapped to integer indices via an [IndexEncoder]
 *
 * @param M the mode dimension (e.g. transport mode).
 * @param I the index type for rows and columns (e.g. ZoneId).
 */
interface MatrixLookup<M, I> {
    operator fun get(mode: M, time: AbsoluteTime): TranslatedDoubleMatrix<I>
}

