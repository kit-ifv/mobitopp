package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.binary.DefaultSimplifiableBinaryWriter
import edu.kit.ifv.domain.simulation.data.person.Person

val BinaryPersonWriter get() = DefaultSimplifiableBinaryWriter<Person>(0)
