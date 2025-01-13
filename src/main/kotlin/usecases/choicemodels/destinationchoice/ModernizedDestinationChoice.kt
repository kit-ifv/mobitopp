package usecases.choicemodels.destinationchoice

import datastructure.StationaryAction
import domain.data.Person
import domain.data.Zone
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.enums.Mode
import domain.enums.ZoneClassification
import domain.location.Location
import domain.location.Metrics
import modeling.models.ChoiceModel
import modeling.models.LogitModel
import units.CurrencyUnit
import usecases.AttractivenessModel
import usecases.choicemodels.ChoiceFilter
import usecases.choicemodels.ChoiceModelModes
import usecases.choicemodels.ILegacyDestinationChoice
import usecases.choicemodels.NoFilter
import usecases.choicemodels.destinationchoice.parameters.BusinessParameters
import usecases.choicemodels.destinationchoice.parameters.DefaultDestinationParameters
import usecases.choicemodels.destinationchoice.parameters.IDefaultDestinationParameters
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
@Suppress("LongParameterList")
class ModernizedDestinationChoice(
    val impedance: Metrics,
    val attractivenessModel: AttractivenessModel,

    val umlands: (
        Location
    ) -> Boolean = { loc -> loc.requireZone().classification == ZoneClassification.OUTLYING_AREA },
    zones: Set<Zone>,
    val modes: ChoiceModelModes,
    val filter: ChoiceFilter<Mode, Person> = NoFilter,
    val parkstress: (Location) -> Double = { 0.0 },
    private val parameterObject: ParameterObject = ParameterObject()
) : ChoiceModel<Person, Location>,
    ILegacyDestinationChoice {
    private val _choices: Set<Location> = zones.map { it.centroid }.toSet()
    private val car = modes.car
    private val publicTransport = modes.publicTransport
    private val pedestrian = modes.pedestrian
    private val bike = modes.bike
    private val passenger = modes.passenger
    override fun choices(agent: Person, time: Time): Set<Location> {
        return _choices
    }
    override fun Collection<Location>.selectDestination(
        person: Person,
        prevActivity: StationaryAction,
        nextActivity: StationaryAction,
        modes: Collection<Mode>,
        randomNumber: Double
    ): Location {
        val target = parameterObject.change(nextActivity.type)
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
                    it.requireZone().id,
                    nextActivity.type
                ) > 0.0
            }

        val build: LogitModel<Person, Location> = object : LogitModel<Person, Location>() {
            override fun utility(agent: Person, choice: Location, time: Time): Double {
                return calculate(target, person, scope, prevActivity.location, choice, nextActivity)
            }

            override val name: String = "Modernized Logit"

            override fun choices(agent: Person, time: Time): Set<Location> {
                return zonesWithAttractivity.toSet()
            }
        }
        val result = build.select(person, zonesWithAttractivity.toSet(), prevActivity.endTime)
        return result
    }

    override fun calculateU_destination(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        availableModes: Collection<Mode>,
        randomNumber: Double
    ): Double {
        val target = parameterObject.change(nextActivity.type)
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
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction
    ): Double {
        val zone = ZoneScope(
            parkstress(destination),
            umlands(destination),
            impedance.distance(origin, destination, car)
        )
        val constant = target.constant.evaluate(person, zone)
        val attractivenessFactor = target.attractiveness.evaluate(person, zone)
        val attractiveness = ln(
            attractivenessModel.attractivenessFor(
                destination.requireZone().id,
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
            ptFactor * ptValue +
            (scope.nextFixedActivityLocation).let {
                carFixFactor * carLogsum.evaluate(scope, zone, destination, it) +
                    ptFixFactor * ptLogsum.evaluate(scope, zone, destination, it)
            }
    }

    @Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
    private val carLogsum = LogsumCalculation().apply {
        register(car) { scope, _, origin, destination ->
            parameterObject.shared.run {
                val travelTime = impedance.duration(
                    origin,
                    destination,
                    car,
                    scope.time
                ) // TODO pass proper time
                    .toDouble(
                        DurationUnit.MINUTES
                    ).coerceAtMost(1000.0)
                val travelCost = impedance.cost(
                    origin,
                    destination,
                    car,
                    scope.time
                ).toDouble(CurrencyUnit.EUROS).coerceAtMost(1000.0)
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
        register(passenger) { scope, _, origin, destination ->
            parameterObject.shared.run {
                asc_mf + b_tt_mf_taxi * (
                    impedance.duration(
                        origin,
                        destination,
                        car,
                        scope.time
                    ) + 3.minutes
                    ).toDouble(
                    DurationUnit.MINUTES
                ).coerceAtMost(1000.0) +
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
        register(publicTransport) { scope, zoneScope, origin, destination ->
            parameterObject.shared.run {
                asc_oev + b_tt_oev * impedance.duration(
                    origin,
                    destination,
                    publicTransport,
                    scope.time
                ).toDouble(DurationUnit.MINUTES).coerceAtMost(1000.0) +
                    b_cost_oev * impedance.cost(
                        origin,
                        destination,
                        publicTransport,
                        scope.time
                    ).toDouble(CurrencyUnit.EUROS).coerceAtMost(1000.0) +
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
        register(pedestrian) { scope, _, origin, destination ->
            parameterObject.shared.run {
                asc_fuss +
                    b_tt_fuss * impedance.duration(
                        origin,
                        destination,
                        pedestrian,
                        scope.time
                    ).toDouble(DurationUnit.MINUTES).coerceAtMost(1000.0) +
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
        register(bike) { scope, _, origin, destination ->
            parameterObject.shared.run {
                asc_rad + b_tt_rad * impedance.duration(
                    origin,
                    destination,
                    bike,
                    scope.time
                ).toDouble(DurationUnit.MINUTES).coerceAtMost(1000.0) +
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
    override val name: String = "Modernized Destination Choice"

    override fun select(agent: Person, choices: Set<Location>, time: Time): Location {
        val prevActivity = agent.schedule.pastActivities().lastOrNull { it <= time }
            ?: throw NoSuchElementException("Activity plan of agent ${agent.id} has no past activities.")
        val nextActivity = agent.schedule.activities().firstOrNull { it > time }
            ?: throw NoSuchElementException("Activity plan of agent ${agent.id} has future planned activity.")
        val modeOptions = filter.filter(modes.options, agent)
        return choices.selectDestination(agent, prevActivity, nextActivity, modeOptions, agent.random.nextDouble())
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

data class ParameterObject(
    val shared: IDefaultDestinationParameters = DefaultDestinationParameters,
    val leisure: DestinationRequirements = LeisureParameters,
    val business: DestinationRequirements = BusinessParameters,
    val service: DestinationRequirements = ServiceParameters,
    val shopping: DestinationRequirements = ShoppingParameters
) {
    fun change(type: ActivityType): DestinationRequirements {
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
}
