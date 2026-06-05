package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.domain.simulation.data.PlannedActivity
import edu.kit.ifv.utils.binary.DefaultBinaryWriter

/**
 * Writes a planned activity to a binary file.
 */
class BinaryActivityWriter : DefaultBinaryWriter<PlannedActivity>()