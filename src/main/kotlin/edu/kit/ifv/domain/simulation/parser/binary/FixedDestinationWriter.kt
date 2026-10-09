package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.binary.DefaultSimplifiableBinaryWriter
import edu.kit.ifv.domain.simulation.data.ActivityLocation

val FixedDestinationWriter get() = DefaultSimplifiableBinaryWriter<ActivityLocation>(0)
