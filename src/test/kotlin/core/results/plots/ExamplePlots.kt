package core.results.plots
//
//import domain.data.Sex
//import modeling.eval.mapping.DemandSimulationResultMapping
//import modeling.eval.mapping.HouseholdMapping
//import modeling.eval.mapping.PersonMapping
//import modeling.steps.asResource
//import usecases.LegacyMode
//import utils.collections.addLabel
//import utils.collections.asBins
//import utils.collections.mapToBins
//import utils.collections.toBins
//import utils.units.sinceStart
//import java.io.File
//import kotlin.math.PI
//import kotlin.math.sin
//import kotlin.test.Test
//import kotlin.time.Duration.Companion.minutes
//
//class ExamplePlots {
//    val resultFile = File("src/test/resources/Rastatt_data/demandsimulationResult.csv")
//    val resultData = DemandSimulationResultMapping(resultFile)
//    val results by lazy { resultData.rows.take(50_000).toList()}
//    val personFile = File("src/test/resources/Rastatt_data/demand-data/person.csv")
//    val persons by lazy {PersonMapping(personFile).rows.take(100_000).toList()}
//    val householdFile = File("src/test/resources/Rastatt_data/demand-data/household.csv")
//    val households by lazy { HouseholdMapping(householdFile).rows.take(40_000).toList()}
//    @Test
//    fun distance_distribution_by_mode() {
//        forData {
//            //personLegs
//            results
//        }.groupBy {
//            //it.leg.transportType
//            LegacyMode.decode(it.legMode)
//
//        }.count {
//
//            //context.impedance.value.distance(
//            //    it.leg.startLocation,
//            //    it.leg.endLocation,
//            //    it.leg.transportType,
//            //).toDouble(DistanceUnit.KILOMETERS).mapToBins(
//            //    distanceBins
//            //)
//            (it.distanceInKm * 100).toInt() * 10
//
//        }.colorByGroup().withStyle {
//            name = "distance distribution by mode"
//            xOrder = Ordering.Ascending()
//            modeling.eval.colorMap = ::modeColor
//            groupLabel = "mode"
//            //xLabel = "distance [km]"
//            xLabel = "distance [m]"
//        }.asHistogram(
//            relative = false,
//            normalize = false,
//        ).plot()
//    }
//
//    @Test
//    fun ganglinie_all() {
//        val builder = forData {
//            //personLegs
//            results
//        }
//            //.count {
//            //it.leg.startTime.minutesSinceStart.toBins(0.0, 60.0*25, 15.0).addLabel { lower, _ ->
//            //lower.minutes.sinceStart.toString()
//            //"${it.tripBeginTime.inWholeHours}h ${(it.tripBeginTime.inWholeMinutes % 60) / 15 * 15}m"
//            .groupBy {_ -> }//.groupBy { "${it.tripBeginTime.inWholeHours}h ${(it.tripBeginTime.inWholeMinutes % 60) / 15 * 15}m" }
//            .aggregateBy( {1.0} ,Aggregation.Sum)
//            .over {
//                //(if (it.tripBeginTime.inWholeHours < 10) "0" else "") +
//                    //"${it.tripBeginTime.inWholeHours}h ${(it.tripBeginTime.inWholeMinutes % 60) / 15 * 15}m"}
//                (it.tripBeginTime.inWholeMinutes / 15 * 15).minutes}
//            .colorByGroup()
//        .withStyle {
//            name = "ganglinie_all"
//            xLabel = "time [hour min]"
//            xOrder = Ordering.Ascending()
//
//        }/*.asLineChart(
//            relative = false,
//            normalize = false,
//
//            compareTo = ComparisonDataSpecification(
//                resource = (0 until 60*24*1).asResource("", ""),
//                groupBy = { _ -> },
//                xAxis = {
//                    it.toLong().toBins(0.0, 60.0*25, 15.0).addLabel { lower, _ ->
//                        lower.minutes.sinceStart.toString()
//                    }
//                },
//                yAxis = { (5000 * sin((1.0/250.0) * it - (PI / 2.0)) + 5000).toInt() }
//            )
//
//        )*/
//            .compareToResource((0 until 60 * 24 * 1).asResource("", ""))
//            .groupBy { _ -> }
//            .plot { (5000 * sin((1.0 / 250.0) * it - (PI / 2.0)) + 5000) }
//            .over({
//                it.toLong().toBins(0.0, 60.0 * 25, 15.0).addLabel { lower, _ ->
//                    lower.minutes.sinceStart.toString()}
//
//            }, { it.lower.minutes })
//
//        builder.asScalableSortableLineChart(relative = false, normalize = false).plot()
//
//    }
//
//    @Test
//    fun `start time distribution by employment`() {
//        forData {
//            //persons
//            results
//        }.groupBy {
//            //it.employment
//            it.employmentType
//        }.count {
//            //it.age.mapToBins(ageBins)
//            (it.tripBeginTime.inWholeMinutes / 15 * 15).minutes
//        }.colorByGroup().withStyle {
//            //xLabel = "age"
//            xLabel = "start time"
//            xOrder = Ordering.Ascending()
//            //name = "age class distribution by employment"
//            name = "start time distribution by employment"
//            groupLabel = "employment"
//        }.asHistogram().plot()
//    }
//
//    @Test
//    fun `age class distribution by employment`(){
//        forData {
//            persons
//        }.groupBy {
//            it.employment
//        }.count {
//            it.age.mapToBins(ageBins)
//        }.colorByGroup().withStyle {
//            xLabel = "age"
//            xOrder = Ordering.Ascending()
//            name = "age class distribution by employment"
//            groupLabel = "employment"
//        }.asHistogram().plot()
//    }
//
//    @Test
//    fun `sex distribution distribution by age class`(){
//        forData {
//            persons
//        }.groupBy {
//            it.age.mapToBins(ageBins)
//        }.count {
//            it.gender
//        }.colorByGroup().withStyle {
//            xLabel = "age"
//            groupOrder = Ordering.Ascending()
//            name = "sex distribution distribution by age class"
//            groupLabel = "age class"
//        }.asHistogram().plot()
//    }
//
//    @Test
//    fun `age distribution by sex`() {
//        forData {
//            persons
//        }.groupBy {
//            it.gender
//        }.count {
//            it.age
//        }.colorByGroup().withStyle {
//            modeling.eval.colorMap = {
//                when(it) {
//                    Sex.FEMALE -> KIT_GREEN
//                    Sex.MALE ->  KIT_BLUE
//                }
//            }
//            xLabel = "age"
//            groupOrder = Ordering.Ascending()
//            name = "age distribution by sex"
//            groupLabel = "sex"
//        }.asLineChart().plot()
//    }
//
//    @Test
//    fun `distribution of commuter ticket ownership`(){
//        forData {
//            persons
//        }.count {
//            it.hasCommuterTicket
//        }.withStyle {
//            xLabel = "has commuter ticket"
//            xOrder = Ordering.Ascending()
//            name = "distribution of commuter ticket ownership"
//        }.asHistogram(
//            normalize = false, relative = false
//        ).plot()
//    }
//
//    @Test
//    fun `distribution of commuter ticket ownership by sex`(){
//        forData {
//            persons
//        }.groupBy {
//            it.hasCommuterTicket
//        }.count {
//            it.gender
//        }.colorByGroup().withStyle {
//            xLabel = "sex"
//            name = "distribution of commuter ticket ownership by sex"
//            groupLabel = "has commuter ticket"
//            modeling.eval.colorMap = ::boolColor
//        }.asHistogram().plot()
//    }
//
//    /*@Test
//    fun `commuter ticket ownership by household vehicle number`(){
//        forData {
//            persons
//        }.groupBy {
//            it.hasCommuterTicket
//        }.count {
//            it.household.members.size
//        }.colorByGroup().withStyle {
//            name = "commuter ticket ownership by household vehicle number"
//            xLabel = "household vehicles"
//            groupLabel = "has commuter ticket"
//            xOrder = Ordering.Ascending()
//            colorMap = ::boolColor
//        }.asHistogram()
//    }*/
//
//    @Test
//    fun `commuter ticket ownership by employment`(){
//        forData {
//            persons
//        }.groupBy {
//            it.hasCommuterTicket
//        }.count {
//            it.employment
//        }.colorByGroup().withStyle {
//            name = "commuter ticket ownership by employment"
//            xLabel = "employment"
//            groupLabel = "has commuter ticket"
//            modeling.eval.colorMap = ::boolColor
//        }.asHistogram().plot()
//    }
//
//    /*
//    fun `commuter ticket ownership by household economical status`(){
//        forData {
//            persons
//        }.groupBy {
//            it.hasCommuterTicket
//        }.count {
//            it.household.economicStatus
//        }.colorByGroup().withStyle {
//            name = "commuter ticket ownership by household economical status"
//            xLabel = "economical status"
//            xOrder = Ordering.Ascending()
//            groupLabel = "has commuter ticket"
//            colorMap = ::boolColor
//        }.asHistogram()
//
//    }*/
//
//    /*
//    @Test
//    fun `number of owned cars by household size`(){
//        forData {
//            households
//        }.groupBy {
//            it.totalNumberOfCars
//        }.count {
//            it.members.size
//        }.colorByGroup().withStyle {
//            xLabel = "household size"
//            xOrder = Ordering.Ascending()
//            groupOrder = Ordering.Ascending()
//            name = "number of owned cars by household size"
//            groupLabel = "number of owned cars"
//        }.asHistogram()
//    }*/
//
//
//    @Test
//    fun `number of owned cars by economical status`(){
//        forData {
//            households
//        }.groupBy {
//            it.totalNumberOfCars
//        }.count {
//            it.economicalStatus
//        }.colorByGroup().withStyle {
//            xLabel = "economical status"
//            xOrder = Ordering.Ascending()
//            groupOrder = Ordering.Ascending()
//            name = "number of owned cars by economical status"
//            groupLabel = "number of owned cars"
//        }.asHistogram().plot()
//    }
//
//}
//private val ageBins = listOf(
//    0 to 7, 7 to 11, 11 to 14, 14 to 18, 18 to 30, 30 to 40,
//    40 to 50, 50 to 60, 60 to 65, 65 to 75, 75 to 80, 80 to 120
//).asBins()
///*
//forData {
//                personLegs
//
//            }.count {
//                it.leg.startTime.minutesSinceStart.toBins(0.0, 60.0*25, 15.0).addLabel { lower, _ ->
//                    lower.minutes.sinceStart.toString()
//                }
//
//            }.withStyle {
//                name = "ganglinie_all"
//                xLabel = "time [hour min]"
//                xOrder = Ordering.Ascending()
//
//            }.asLineChart(
//                relative = false,
//                normalize = false,
//
//                compareTo = ComparisonDataSpecification(
//                    resource = (0 until 60*24*1).asResource("", ""),
//                    groupBy = { _ -> },
//                    xAxis = {
//                        it.toLong().toBins(0.0, 60.0*25, 15.0).addLabel { lower, _ ->
//                            lower.minutes.sinceStart.toString()
//                        }
//                    },
//                    yAxis = { (5000 * sin((1.0/250.0) * it - (PI / 2.0)) + 5000).toInt() }
//                )
//
//            )
//
//
//forData {
//                personLegs
//            }.groupBy {
//                it.leg.transportType
//            }.count {
//                it.person.age.toBins(min = 0.0, max = 120.0, binSize = 10.0)
//            }.colorByGroup().withStyle {
//                name = "mode over age"
//                xOrder = Ordering.Ascending()
//                colorMap = ::modeColor
//                groupLabel = "mode"
//                xLabel = "age"
//            }.asHistogram(
//                relative = false,
//                normalize = true,
//            )
//
//
//forData {
//                persons
//            }.groupBy {
//                it.employment
//            }.count {
//                it.age.mapToBins(ageBins)
//            }.colorByGroup().withStyle {
//                xLabel = "age"
//                xOrder = Ordering.Ascending()
//                name = "age class distribution by employment"
//                groupLabel = "employment"
//            }.asHistogram()
//
//
//
//forData {
//                persons
//            }.groupBy {
//                it.age.mapToBins(ageBins)
//            }.count {
//                it.sex
//            }.colorByGroup().withStyle {
//                xLabel = "age"
//                groupOrder = Ordering.Ascending()
//                name = "sex distribution distribution by age class"
//                groupLabel = "age class"
//            }.asHistogram()
//
//
//forData {
//                persons
//            }.groupBy {
//                it.sex
//            }.count {
//                it.age
//            }.colorByGroup().withStyle {
//                colorMap = {
//                    when(it) {
//                        Sex.FEMALE -> KIT_GREEN
//                        Sex.MALE ->  KIT_BLUE
//                    }
//                }
//                xLabel = "age"
//                groupOrder = Ordering.Ascending()
//                name = "age distribution by sex"
//                groupLabel = "sex"
//            }.asLineChart()
//
//
//forData {
//                households
//            }.count {
//                it.cars.size
//            }.withStyle {
//                xLabel = "number of owned cars"
//                xOrder = Ordering.Ascending()
//                name = "distribution of owned vehicles per household"
//            }.asHistogram(
//                normalize = false,
//                relative = true,
//            )
//
//
//
//forData {
//                households
//            }.groupBy {
//                it.cars.size
//            }.count {
//                it.members.filter(Person::hasLicense).size
//            }.colorByGroup().withStyle {
//                xLabel = "number of drivers licenses"
//                xOrder = Ordering.Ascending()
//                groupOrder = Ordering.Ascending()
//                name = "number of owned cars by number of drivers licenses per household"
//                groupLabel = "number of owned cars"
//            }.asHistogram()
//
//
//            forData {
//                households
//            }.groupBy {
//                it.cars.size
//            }.count {
//                it.members.size
//            }.colorByGroup().withStyle {
//                xLabel = "household size"
//                xOrder = Ordering.Ascending()
//                groupOrder = Ordering.Ascending()
//                name = "number of owned cars by household size"
//                groupLabel = "number of owned cars"
//            }.asHistogram()
//
//
//
//forData {
//                households
//            }.groupBy {
//                it.cars.size
//            }.count {
//                it.economicStatus
//            }.colorByGroup().withStyle {
//                xLabel = "economical status"
//                xOrder = Ordering.Ascending()
//                groupOrder = Ordering.Ascending()
//                name = "number of owned cars by economical status"
//                groupLabel = "number of owned cars"
//            }.asHistogram()
//
//
//
//forData {
//                persons
//            }.count {
//                it.hasCommuterTicket
//            }.withStyle {
//                xLabel = "has commuter ticket"
//                xOrder = Ordering.Ascending()
//                name = "distribution of commuter ticket ownership"
//            }.asHistogram(
//                normalize = false, relative = false
//            )
//
//
//forData {
//                persons
//            }.groupBy {
//                it.hasCommuterTicket
//            }.count {
//                it.sex
//            }.colorByGroup().withStyle {
//                xLabel = "sex"
//                name = "distribution of commuter ticket ownership by sex"
//                groupLabel = "has commuter ticket"
//                colorMap = ::boolColor
//            }.asHistogram()
//
//
//
//
//forData {
//                persons
//            }.groupBy {
//                it.hasCommuterTicket
//            }.count {
//                it.sex
//            }.colorByGroup().withStyle {
//                xLabel = "has commuter ticket"
//                xOrder = Ordering.Ascending()
//                name = "distribution of commuter ticket ownership by sex"
//                groupLabel = "sex"
//                colorMap = ::boolColor
//            }.asHistogram()
//
//
//
//forData {
//                persons
//            }.groupBy {
//                it.hasCommuterTicket
//            }.count {
//                it.household.members.size
//            }.colorByGroup().withStyle {
//                name = "commuter ticket ownership by household vehicle number"
//                xLabel = "household vehicles"
//                groupLabel = "has commuter ticket"
//                xOrder = Ordering.Ascending()
//                colorMap = ::boolColor
//            }.asHistogram()
//
//
//
//
//forData {
//                persons
//            }.groupBy {
//                it.hasCommuterTicket
//            }.count {
//                it.employment
//            }.colorByGroup().withStyle {
//                name = "commuter ticket ownership by employment"
//                xLabel = "employment"
//                groupLabel = "has commuter ticket"
//                colorMap = ::boolColor
//            }.asHistogram()
//
//
//
//
//forData {
//                persons
//            }.groupBy {
//                it.hasCommuterTicket
//            }.count {
//                it.household.economicStatus
//            }.colorByGroup().withStyle {
//                name = "commuter ticket ownership by household economical status"
//                xLabel = "economical status"
//                xOrder = Ordering.Ascending()
//                groupLabel = "has commuter ticket"
//                colorMap = ::boolColor
//            }.asHistogram()
//
//
//
//
//forData {
//                persons
//            }.groupBy {
//                personMemberships(it)
//            }.count {
//                it.household.cars.size
//            }.colorByGroup().withStyle {
//                name = "m emberships by number of owned household vehicles"
//                xLabel = "number of vehicles"
//                groupLabel = "memberships"
//                xOrder = Ordering.Ascending()
//            }.asHistogram()
//* */