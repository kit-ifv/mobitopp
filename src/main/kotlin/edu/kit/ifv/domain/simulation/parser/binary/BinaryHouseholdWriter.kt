package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.domain.simulation.data.household.Household
import edu.kit.ifv.utils.binary.DefaultBinaryWriter

/**
 * Writes a collection of households into a binary file.
 */
class BinaryHouseholdWriter : DefaultBinaryWriter<Household>()