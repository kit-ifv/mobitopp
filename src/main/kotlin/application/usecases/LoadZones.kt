package application.usecases

import application.synthesis.FinalIdResourceStep
import domain.region.MutableZoneData
import domain.region.ZoneData
import utils.csv.CsvParser
import utils.csv.CsvParserBuilder
import java.io.File

fun loadZonesStep(
    file: File
) = FinalIdResourceStep<DefaultContext, ZoneData>(
    name= "",
    resource = { createZoneParser().parse(file).map { it.build() } },
    setter = { c, r -> c.zoneRepo = r}
)

fun createZoneParser(): CsvParser<MutableZoneData> =
    CsvParserBuilder{ _ -> MutableZoneData() }
        .addLongColumn("zoneId") { zone, id -> zone.id = id }
        .addStringColumn("name") { zone, name -> zone.name = name }
        .addDoubleColumn("relief") { zone, relief -> zone.relief = relief }
        .build()

