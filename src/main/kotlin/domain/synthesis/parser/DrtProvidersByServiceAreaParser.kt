package domain.synthesis.parser

import domain.shared.enums.Mode
import domain.shared.location.ZoneId
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.MutableDrtProviderData
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.int
import utils.csv.long

// id providers
object GlobalDrtProviderIdCounter : (Row) -> DrtProviderId, () -> DrtProviderId {
    private var counter = 0L
    override operator fun invoke(row: Row) = invoke() // TODO take any
    override fun invoke(): DrtProviderId = DrtProviderId(counter++)
}

data class DrtProviderByAreaCsvConfig(
    var columns: DrtProviderByAreaCsvColumns = DrtProviderByAreaCsvColumns(),
    var drtMode: Mode,

    var getZone: GetZone,
    var providerIdSource: (Row) -> DrtProviderId,

    var operatingHours: IntRange,
    var errorHandling: ErrorHandling,
    val seed: Long,
)

data class DrtProviderByAreaCsvColumns(
    val provider: String = "provider",
    val numVehicles: String = "numVehicles",
    val zone: String = "zone",
)

fun createDrtProvidersByAreaParser(csvConfig: DrtProviderByAreaCsvConfig): CsvParser<MutableDrtProviderData> =
    csvConfig.run {
        val providers = mutableMapOf<String, MutableDrtProviderData>()

        CsvParser.Companion { row ->

            val providerName = row(columns.provider)
            var newProvider = false
            val provider = providers.computeIfAbsent(providerName) { n ->
                newProvider = true
                MutableDrtProviderData(providerIdSource(row)) {
                    this.name = n
                    this.mode = drtMode
                    this.operatingHours = csvConfig.operatingHours
                }
            }

            val initVehicles = row.int(columns.numVehicles)
            val zone = getZone(ZoneId(row.long(columns.zone)))

            provider.serviceArea.add(zone.id)
            if (initVehicles > 0) {
                provider.initVehicles[zone.id] = initVehicles
            }

            provider.takeIf { newProvider }
        }
    }
