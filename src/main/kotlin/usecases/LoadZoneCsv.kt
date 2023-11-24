package usecases

import Builder
import domain.data.AreaType
import domain.data.Location
import domain.data.MutableHackyZoneData
import domain.data.RoadPosition
import domain.data.ZoneClassification
import domain.data.ZoneData
import modeling.synthesis.BaseContext
import modeling.synthesis.Resource
import modeling.synthesis.Synthesis
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.CsvParserBuilder
import utils.csv.CsvReader
import utils.csv.SEMICOLON
import utils.csv.column
import utils.csv.property
import utils.units.Coordinate
import utils.units.Distance
import utils.units.DistanceUnit
import utils.units.GPSCoordinate
import utils.units.UnitIntervalValue
import utils.units.meters
import utils.units.share
import utils.units.toDistance
import java.io.File
import java.lang.UnsupportedOperationException


fun <S, C> S.loadZoneCsv(
    parser: CsvParser<MutableHackyZoneData>,
    file: File,
    delimiter: String = SEMICOLON
) where S: Synthesis<C>, C: BaseContext {

    val r = CsvResource(file, parser, delimiter)

    val r2 = SeqResource(
        r.elements.map { it.build() as ZoneData }
    )

    this.addFinalIdResource(file.name, r2) {
        c, r -> c.zones = r
    }

}

class SeqResource<E>(override val elements: Sequence<E>): Resource<E>

class CsvResource<E> (
    val file: File,
    val parser: CsvParser<E>,
    val delimiter: String = SEMICOLON

): Resource<E> {
    override val elements: Sequence<E>
        get() = parser.parse(CsvReader.of(file))

}

class FinalCsvResource<E, B: Builder<E>> (
    val file: File,
    val parser: CsvParser<B>
): Resource<E> {
    override val elements: Sequence<E>
        get() = parser.parse(CsvReader.of(file)).map { it.build() }

}

fun zoneParser(reliefUnit: DistanceUnit = DistanceUnit.METERS) : CsvParser<MutableHackyZoneData> =
    CsvParserBuilder{ MutableHackyZoneData() }
        .long.property("id") { e, l -> e.visumId = l}
        .string.property("name") { e, s -> e.name = s}
        .int.property("areaType") { e, i -> e.areaType = AreaType.decode(i) }
        .int.property("regionType") { e, i -> e.regionType = i }
        .string.property("classification") { e, s -> e.classification = when(s) {
            "studyArea" -> ZoneClassification.STUDY_AREA
            "outlyingArea" -> ZoneClassification.OUTLYING_AREA
            "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA
            else -> throw UnsupportedOperationException("Sorry this is not existing")
        } }
        .int.property("parkingPlaces") { e, i -> e.parkingPlaces = i }
        .string.property("centroidLocation") { e, s -> e.centroid = s.parseVisumLocation() }
        .boolean.property("isDestination") { e, b -> e.isDestination = b }
        .double.property("relief") { e, d -> e.relief = d.toDistance(reliefUnit) }
        .onErrorUse(ErrorHandling.WARN_DROP)
        .build()

@Suppress("MagicNumber")
fun String.parseVisumLocation(): RoadPosition {
    val res = this.removeSurrounding(prefix= "(", suffix=")").split(":", ",").map { it.trim() }
    require(res.size == 4)

    class Temp(
        override val coordinate: Coordinate,
        override val roadAccess: UnitIntervalValue,
        override val road: Long
            ) : RoadPosition {
        override fun distance(other: Location): Distance {
            throw UnsupportedOperationException()
        }
            }
    return Temp(GPSCoordinate.degrees(res[1].toDouble(), res[0].toDouble()), res[3].toDouble().share(), res[2].toLong())
}
