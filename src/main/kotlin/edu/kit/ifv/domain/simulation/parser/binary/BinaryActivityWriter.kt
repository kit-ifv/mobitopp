package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.binary.DefaultSimplifiableBinaryWriter
import edu.kit.ifv.domain.simulation.data.PlannedActivity

/**
 * Writes a planned activity to a binary file.
 */
val BinaryActivityWriter get() = DefaultSimplifiableBinaryWriter<PlannedActivity>(0)
