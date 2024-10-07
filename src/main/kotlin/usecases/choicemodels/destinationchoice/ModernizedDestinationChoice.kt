package usecases.choicemodels.destinationchoice

import datastructure.StationaryAction
import domain.data.Person
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.enums.Mode
import domain.enums.StandardMode
import domain.enums.ZoneClassification
import domain.location.Location
import domain.location.Metrics
import domain.location.ZoneLocation
import modeling.models.LogitModel
import units.CurrencyUnit
import units.euros
import usecases.AttractivenessModel
import usecases.choicemodels.ILegacyDestinationChoice
import usecases.choicemodels.destinationchoice.parameters.BusinessParameters
import usecases.choicemodels.destinationchoice.parameters.DefaultDestinationParameters
import usecases.choicemodels.destinationchoice.parameters.LeisureParameters
import usecases.choicemodels.destinationchoice.parameters.ServiceParameters
import usecases.choicemodels.destinationchoice.parameters.ShoppingParameters
import usecases.choicemodels.nextFixedActivity
import utils.units.AbsoluteTime
import utils.units.Time
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

class ModernizedDestinationChoice(
    val impedance: Metrics,
    val attractivenessModel: AttractivenessModel,
    val parkstress: (Location) -> Double = { 0.0 },
    val umlands: (
        Location
    ) -> Boolean = { loc -> (loc as ZoneLocation).zone.classification == ZoneClassification.OUTLYING_AREA }
) :
    ILegacyDestinationChoice {

    override fun Collection<ZoneLocation>.selectDestination(
        person: Person,
        prevActivity: StationaryAction,
        nextActivity: StationaryAction,
        modes: Collection<Mode>,
        randomNumber: Double
    ): ZoneLocation {
        val target = change(nextActivity.type)
        val scope = PersonScope(
            person.age,
            person.employment,
            person.hasCommuterTicket,
            person.household.cars.size >= person.household.members.size,
            person.household.economicStatus,
            nextActivity.type,
            modes,
            prevActivity.endTime,
            person.nextFixedActivity()?.location ?: person.household.location
        )

        val zonesWithAttractivity =
            filter {
                attractivenessModel.attractivenessFor(
                    it.zone.id,
                    nextActivity.type
                ) > 0.0
            }

        val build: LogitModel<Person, ZoneLocation> = object : LogitModel<Person, ZoneLocation>() {
            override fun utility(agent: Person, choice: ZoneLocation, time: Time): Double {
                return calculate(target, person, scope, prevActivity.location as ZoneLocation, choice, nextActivity)
            }

            override val name: String = "Modernized Logit"

            override fun choices(agent: Person, time: Time): Set<ZoneLocation> {
                return zonesWithAttractivity.toSet()
            }
        }
        val result = build.select(person, zonesWithAttractivity.toSet(), prevActivity.endTime)
        return result
    }

    override fun calculateU_destination(
        category: ZoneLocation,
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        availableModes: Collection<Mode>,
        randomNumber: Double
    ): Double {
        val target = change(nextActivity.type)
        val scope = PersonScope(
            person.age,
            person.employment,
            person.hasCommuterTicket,
            person.household.cars.size >= person.household.members.size,
            person.household.economicStatus,
            nextActivity.type,
            availableModes,
            time,
            person.nextFixedActivity()?.location ?: person.household.location

        )
        return calculate(target, person, scope, origin, destination, nextActivity)
    }

    @Suppress("LongParameterList")
    private fun calculate(
        target: DestinationRequirements,
        person: Person,
        scope: PersonScope,
        origin: ZoneLocation,
        destination: ZoneLocation,
        nextActivity: StationaryAction
    ): Double {
        val zone = ZoneScope(
            parkstress(destination),
            umlands(destination),
            impedance.distance(origin, destination, StandardMode.CAR)
        )
        val constant = target.constant.evaluate(person, zone)
        val attractivenessFactor = target.attractiveness.evaluate(person, zone)
        val attractiveness = ln(
            attractivenessModel.attractivenessFor(
                destination.zone.id,
                nextActivity.type
            ).coerceAtMost(target.attractiveness.maxAttractiveness)
        )
        val carFactor = target.carLogsum.evaluate(person, zone)
        val carLogsumVal = carLogsum.evaluate(scope, zone, origin, destination)
        val carFixFactor = target.carLogsumFix.evaluate(person, zone)
        val ptFactor = target.flexLogsum.evaluate(person, zone)
        val ptValue = ptLogsum.evaluate(scope, zone, origin, destination)
        val ptFixFactor = target.flexFixLogsum.evaluate(person, zone)
        return constant +
            attractivenessFactor * attractiveness +
            carFactor * carLogsumVal +
            (scope.nextFixedActivityLocation).let {
                carFixFactor * carLogsum.evaluate(scope, zone, destination, it)
            } +
            ptFactor * ptValue +
            (scope.nextFixedActivityLocation).let {
                ptFixFactor * ptLogsum.evaluate(scope, zone, destination, it)
            }
    }

    private fun change(type: ActivityType): DestinationRequirements {
        return when (type) {
            LegacyActivityType.BUSINESS,
            LegacyActivityType.BUSINESS_TRAVEL,
            LegacyActivityType.BUSINESS_OUT,
            LegacyActivityType.BUSINESS_TO_WORK -> BusinessParameters

            LegacyActivityType.SHOPPING,
            LegacyActivityType.PRIVATE_BUSINESS,
            LegacyActivityType.SHOPPING_OTHER,
            LegacyActivityType.SHOPPING_DAILY -> ShoppingParameters

            LegacyActivityType.SERVICE -> ServiceParameters
            else -> LeisureParameters
        }
    }

    @Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
    private val carLogsum = LogsumCalculation().apply {
        register(StandardMode.CAR) { scope, _, origin, destination ->
            DefaultDestinationParameters.run {
                val travelTime = impedance.duration(
                    origin,
                    destination,
                    StandardMode.CAR,
                    scope.time
                ) // TODO pass proper time
                    .coerceAtMost(1000.minutes).toDouble(
                        DurationUnit.MINUTES
                    )
                val travelCost = impedance.cost(
                    origin,
                    destination,
                    StandardMode.CAR,
                    scope.time
                ).coerceAtMost(1000.euros).toDouble(CurrencyUnit.EUROS)
                asc_pkw + b_tt_pkw * travelTime + b_cost_pkw * travelCost +
                    when (scope.nextActivityType) {
                        LegacyActivityType.WORK -> b_arb_on_pkw
                        LegacyActivityType.BUSINESS -> b_dienst_on_pkw
                        LegacyActivityType.SERVICE -> b_service_on_pkw
                        LegacyActivityType.LEISURE,
                        LegacyActivityType.LEISURE_INDOOR,
                        LegacyActivityType.LEISURE_OUTDOOR,
                        LegacyActivityType.LEISURE_OTHER,
                        LegacyActivityType.LEISURE_WALK,
                        LegacyActivityType.LEISURE_SIGHTSEEING,
                        LegacyActivityType.PRIVATE_VISIT -> b_freizeit_on_pkw

                        else -> 0.0
                    }
            }
        }
        register(StandardMode.PASSENGER) { scope, _, origin, destination ->
            DefaultDestinationParameters.run {
                asc_mf + b_tt_mf_taxi * (
                    impedance.duration(
                        origin,
                        destination,
                        StandardMode.CAR,
                        scope.time
                    ) + 3.minutes
                    ).coerceAtMost(1000.minutes).toDouble(
                    DurationUnit.MINUTES
                ) +
                    when (scope.nextActivityType) {
                        LegacyActivityType.WORK -> b_arb_on_mf
                        LegacyActivityType.BUSINESS -> b_dienst_on_mf
                        LegacyActivityType.SERVICE -> b_service_on_mf
                        LegacyActivityType.HOME -> b_home_on_mf
                        else -> 0.0
                    }
            }
        }
    }

    @Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
    private val ptLogsum = LogsumCalculation().apply {
        register(StandardMode.PUBLICTRANSPORT) { scope, zoneScope, origin, destination ->
            DefaultDestinationParameters.run {
                asc_oev + b_tt_oev * impedance.duration(
                    origin,
                    destination,
                    StandardMode.PUBLICTRANSPORT,
                    scope.time
                ).coerceAtMost(1000.minutes).toDouble(DurationUnit.MINUTES) +
                    b_cost_oev * impedance.cost(
                        origin,
                        destination,
                        StandardMode.PUBLICTRANSPORT,
                        scope.time
                    ).coerceAtMost(1000.euros).toDouble(CurrencyUnit.EUROS) +
                    when (scope.nextActivityType) {
                        LegacyActivityType.WORK -> b_arb_on_oev
                        LegacyActivityType.BUSINESS -> b_dienst_on_oev
                        LegacyActivityType.SERVICE -> b_service_on_oev
                        LegacyActivityType.LEISURE,
                        LegacyActivityType.LEISURE_INDOOR,
                        LegacyActivityType.LEISURE_OUTDOOR,
                        LegacyActivityType.LEISURE_OTHER,
                        LegacyActivityType.LEISURE_WALK,
                        LegacyActivityType.LEISURE_SIGHTSEEING,
                        LegacyActivityType.PRIVATE_VISIT -> b_freizeit_on_oev

                        else -> 0.0
                    } +
                    b_park_oev * (
                        if (zoneScope.parkingPressure in 0.0..999.0) {
                            zoneScope.parkingPressure.coerceAtMost(50.0)
                                .pow(elasticity_park_oev)
                        } else {
                            0.0
                        }
                        )
            }
        }
        register(StandardMode.PEDESTRIAN) { scope, _, origin, destination ->
            DefaultDestinationParameters.run {
                asc_fuss +
                    b_tt_fuss * impedance.duration(
                        origin,
                        destination,
                        StandardMode.PEDESTRIAN,
                        scope.time
                    ).coerceAtMost(1000.minutes).toDouble(DurationUnit.MINUTES) +
                    when (scope.nextActivityType) {
                        LegacyActivityType.WORK -> b_arb_on_fuss
                        LegacyActivityType.BUSINESS -> b_dienst_on_fuss
                        LegacyActivityType.SERVICE -> b_service_on_fuss
                        LegacyActivityType.LEISURE,
                        LegacyActivityType.LEISURE_INDOOR,
                        LegacyActivityType.LEISURE_OUTDOOR,
                        LegacyActivityType.LEISURE_OTHER,
                        LegacyActivityType.LEISURE_WALK,
                        LegacyActivityType.LEISURE_SIGHTSEEING,
                        LegacyActivityType.PRIVATE_VISIT -> b_freizeit_on_fuss

                        else -> 0.0
                    }
            }
        }
        register(StandardMode.BIKE) { scope, _, origin, destination ->
            DefaultDestinationParameters.run {
                asc_rad + b_tt_rad * impedance.duration(
                    origin,
                    destination,
                    StandardMode.BIKE,
                    scope.time
                ).coerceAtMost(1000.minutes).toDouble(DurationUnit.MINUTES) +
                    when (scope.nextActivityType) {
                        LegacyActivityType.WORK -> b_arb_on_rad
                        LegacyActivityType.BUSINESS -> b_dienst_on_rad
                        LegacyActivityType.SERVICE -> b_service_on_rad
                        LegacyActivityType.LEISURE,
                        LegacyActivityType.LEISURE_INDOOR,
                        LegacyActivityType.LEISURE_OUTDOOR,
                        LegacyActivityType.LEISURE_OTHER,
                        LegacyActivityType.LEISURE_WALK,
                        LegacyActivityType.LEISURE_SIGHTSEEING,
                        LegacyActivityType.PRIVATE_VISIT -> b_freizeit_on_rad

                        else -> 0.0
                    }
            }
        }
    }
}

/**
 * Logsum calculation from legacy transmove code
 *
 * @constructor Creates an empty logsum calculation at which additional modes can be registered.
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
class LogsumCalculation {
    private val registeredModes: MutableMap<Mode, (PersonScope, ZoneScope, Location, Location) -> Double> =
        mutableMapOf()

    fun register(mode: Mode, functor: (PersonScope, ZoneScope, Location, Location) -> Double) {
        registeredModes[mode] = functor
    }

    fun evaluate(
        personScope: PersonScope,
        zoneScope: ZoneScope,
        origin: Location,
        destination: Location
    ): Double {
        val targets = registeredModes.filter { it.key in personScope.availableModes }
        if (targets.isEmpty()) return -50.0

        return targets.size * ln(
            targets.values.sumOf {
                exp(
                    it(personScope, zoneScope, origin, destination)
                )
            }
        )
    }
}
