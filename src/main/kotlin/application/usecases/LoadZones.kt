package application.usecases

//fun loadZonesStep(
//    file: File
//) = FinalIdResource<DefaultContext, ZoneData>(
//    name= "",
//    resource = { createZoneParser().parse(file).map { it.build() } },
//    setter = { c, r -> c.zoneRepo = r}
//)

//fun createZoneParser(): CsvParser<MutableZoneData> =
//    CsvParserBuilder{ _ -> MutableZoneData() }
//        .addColumn("zoneId") { zone: MutableZoneData, id: String -> zone.id = id; zone }
//        .addStringColumn("name") { zone, name -> zone.name = name }
//        .addDoubleColumn("relief") { zone, relief -> zone.relief = relief }
//        .build()

