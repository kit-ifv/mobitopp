package application.steps.parser.csv

import application.steps.HasZoneRepo
import application.steps.RegionCodesConfig
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import core.modelsteps.Context
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.mutableRepositoryScope
import domain.shared.enums.areatype.RegionType
import domain.shared.location.MutableZone
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasRoadAccess
import domain.shared.location.parseRoadPositionWGS
import domain.synthesis.parser.ZoneColumns
import domain.synthesis.parser.ZoneCsvConfig
import domain.synthesis.parser.binary.BinaryZoneReader
import domain.synthesis.parser.binary.BinaryZoneWriter
import domain.synthesis.parser.zoneCsvParser
import edu.kit.ifv.units.DistanceUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import java.nio.file.Path


context(config: CFG)
fun <CTXT, CFG> CTXT.zones(
    scope: context(MutableRepository<MutableZone, ZoneId>, CFG) CTXT.() -> Unit
) where CTXT : HasZoneRepo<Zone> = mutableRepositoryScope(
    "zones", this::zoneRepository.setter, scope
)

context(repository: MutableRepository<MutableZone, ZoneId>)
fun <C: Context> C.loadZones(
    resource: Resource<MutableZone>,
) = addResourceStep<C, MutableZone, ZoneId>(
    name = "load zones from ${resource.name}",
    resource = resource,
)


context(config: CFG)
fun <C: Context, CFG> C.zoneCsv(
    parser: CsvParser<MutableZone> = zoneCsvParser(),
    path: Path = config.sourceFiles.zonesCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableZone>? = binaryZoneFormat()
): Resource<MutableZone>
where CFG: SourceFilesConfig, CFG: UnitConfig, CFG: RegionCodesConfig
//TODO config as required upper bound type in context
    = CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    }

context(config: CFG)
fun <C: Context, CFG> C.binaryZoneFormat(): BinaryCacheConfig<MutableZone>
where CFG: SourceFilesConfig, CFG: RegionCodesConfig
{
    return BinaryCacheConfig<MutableZone>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryZoneReader(
            seed = config.seed,
            regionCode = config.regionTypeCodes
        ),
        binaryWriter = BinaryZoneWriter()
    )
}

context(config: CFG)
fun <C: Context, CFG> C.zoneCsvParser(
    customizeCsvConfig: ZoneCsvConfig.() -> Unit = {}
): CsvParser<MutableZone>
where CFG: SourceFilesConfig, CFG: UnitConfig, CFG: RegionCodesConfig
= zoneCsvParser(
    ZoneCsvConfig(
        columns = ZoneColumns(),
        centroidParser = String::parseRoadPositionWGS,
        reliefUnit = config.distanceUnit,
        regionTypeCodes = config.regionTypeCodes,
        errorHandling = config.errorHandling,
        seed = config.seed
    ).also {
        it.customizeCsvConfig()
    }
)


//fun cheatyDefaultCsvParser(
//    errorHandling: ErrorHandling = ErrorHandling.WARNING,
//    seed: Long = 1L
//): DefaultCsvParser<MutableZone> {
//    val csvParser = CsvParser(errorHandling) { row ->
//        MutableZone(
//            id = ZoneId(row.long("id")),
//            centroid = Location.BIELEFELD,
//            seed = seed
//        ) {
//            visumId = row.long("id")
//            name = row("zone_name")
//            regionType = RegioStaR17.REGIOPOLE
//            classification = ZoneClassification.STUDY_AREA
//            parkingPlaces = 0
//            isDestination = true
//            relief = 0.meters
//        }
//    }
//
//    return csvParser
//}

