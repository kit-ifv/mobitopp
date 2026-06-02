package domain.shared.location

import org.geotools.api.referencing.cs.CoordinateSystem

fun CoordinateSystem.axisUnits(): Set<String> = (0 until dimension).map { this.getAxis(it).unit.name }.toSet()
