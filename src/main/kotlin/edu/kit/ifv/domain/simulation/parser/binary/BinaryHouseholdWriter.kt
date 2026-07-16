package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.binary.DefaultSimplifiableBinaryWriter
import edu.kit.ifv.domain.simulation.data.household.Household

/**
 * Writes a collection of households into a binary file.
 */
val BinaryHouseholdWriter get() = DefaultSimplifiableBinaryWriter<Household>(0)
