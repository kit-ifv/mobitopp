package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.binary.DefaultSimplifiableBinaryWriter
import edu.kit.ifv.domain.simulation.data.car.PrivateCar

val BinaryCarWriter get() = DefaultSimplifiableBinaryWriter<PrivateCar>(0)
