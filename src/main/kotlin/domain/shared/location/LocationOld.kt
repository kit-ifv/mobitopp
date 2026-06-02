package domain.shared.location

import edu.kit.ifv.units.WGS84Coordinate
import org.geotools.api.referencing.cs.CoordinateSystem
import org.locationtech.jts.geom.Point

@Suppress("MagicNumber")
fun WGS84Coordinate.toPoint(): Point = PointCreator.createWGS(x, y)

fun CoordinateSystem.axisUnits(): Set<String> = (0 until dimension).map { this.getAxis(it).unit.name }.toSet()

