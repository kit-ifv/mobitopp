package choicemodels

import datastructure.StationaryAction
import domain.data.Person
import domain.data.lastTransportMode
import domain.enums.Mode
import domain.enums.StandardMode
import domain.location.Metrics
import domain.location.ZoneLocation
import units.CurrencyUnit
import units.Distance
import units.euros
import usecases.AttractivenessModel
import usecases.choicemodels.BIKESHARING_KEY
import usecases.choicemodels.BIKE_KEY
import usecases.choicemodels.CARSHARING_FREE_KEY
import usecases.choicemodels.CARSHARING_STATION_KEY
import usecases.choicemodels.CAR_KEY
import usecases.choicemodels.D
import usecases.choicemodels.E_SCOOTER_KEY
import usecases.choicemodels.IGeneratedHcUtilityFunction
import usecases.choicemodels.PASSENGER_KEY
import usecases.choicemodels.PEDESTRIAN_KEY
import usecases.choicemodels.PUBLICTRANSPORT_KEY
import usecases.choicemodels.RIDE_POOLING_KEY
import usecases.choicemodels.TAXI_KEY
import usecases.choicemodels.modechoice.CombinedScope
import usecases.choicemodels.modechoice.ModePersonScope
import usecases.choicemodels.modechoice.ModeZoneScope
import usecases.choicemodels.modechoice.parameters.BikeParameters
import usecases.choicemodels.modechoice.parameters.BikesharingParameters
import usecases.choicemodels.modechoice.parameters.CarParameters
import usecases.choicemodels.modechoice.parameters.CarsharingFreeFloatingParameters
import usecases.choicemodels.modechoice.parameters.CarsharingStationParameters
import usecases.choicemodels.modechoice.parameters.EScooterParameters
import usecases.choicemodels.modechoice.parameters.MoiaParameters
import usecases.choicemodels.modechoice.parameters.PassengerParameters
import usecases.choicemodels.modechoice.parameters.PedestrianParameters
import usecases.choicemodels.modechoice.parameters.PublicTransportParameters
import usecases.choicemodels.modechoice.parameters.TaxiParameters
import usecases.choicemodels.nextFixedActivity
import usecases.parkingPressure
import utils.CodePlan
import utils.units.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

class ModernizedModeUtility(
    override val modes: CodePlan<Mode>,
    val attractivenessModel: AttractivenessModel
) : IGeneratedHcUtilityFunction {
    val pedestrianParameters = PedestrianParameters

    lateinit var utilityScope: CombinedScope

    private lateinit var relevant: Relevant

    private data class Relevant(
        val person: Person,
        val origin: ZoneLocation,
        val destination: ZoneLocation,
        val previousActivity: StationaryAction,
        val nextActivity: StationaryAction

    )

    // TODO all of these could be appended to their corresponding parameter set
    val taxi = modeMap[TAXI_KEY]!!
    val ridepooling = modeMap[RIDE_POOLING_KEY]!!
    val bikesharing = modeMap[BIKESHARING_KEY]!!
    val csff = modeMap[CARSHARING_FREE_KEY]!!
    val cssb = modeMap[CARSHARING_STATION_KEY]!!
    val escooter = modeMap[E_SCOOTER_KEY]!!
    val car = modeMap[CAR_KEY]!!
    val passenger = modeMap[PASSENGER_KEY]!!
    val bike = modeMap[BIKE_KEY]!!
    val pt = modeMap[PUBLICTRANSPORT_KEY]!!
    val ped = modeMap[PEDESTRIAN_KEY]!!

    private fun setUtilityScope(
        person: Person,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        distance: Distance,
        targetZone: ZoneLocation
    ) {
        utilityScope = CombinedScope(
            ModePersonScope(
                person, nextActivity, person.lastTransportMode(previousActivity) ?: StandardMode.UNKNOWN,

            ),

            ModeZoneScope(
                previousActivity.endTime, distance,
                attractivenessModel.parkingPressure(targetZone.zone)
            )
        )
    }

    @Suppress("LongParameterList")
    fun updateUtilityScope(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        impedance: Metrics
    ) {
        if (this::relevant.isInitialized && relevant == Relevant(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity
            )
        ) {
            return
        }
        setUtilityScope(
            person,
            previousActivity,
            nextActivity,
            impedance.distance(origin, destination, StandardMode.CAR),
            destination
        )
    }

    override fun calculateU_fuss(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)

        val duration = impedance.duration(
            origin,
            destination,
            ped,
            previousActivity.endTime
        )
        return pedestrianParameters.evaluate(utilityScope, duration)
    }

    val bikeParameters = BikeParameters

    override fun calculateU_rad(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val travelTime: Duration = impedance.duration(origin, destination, bike, previousActivity.endTime)
        val travelTimeFixed: Duration = person.nextFixedActivity()?.let {
            impedance.duration(destination, it.location, bike, previousActivity.endTime)
        } ?: Duration.ZERO
        val maxTravelTime = max(travelTimeFixed, travelTime)
        return bikeParameters.evaluate(utilityScope, maxTravelTime)
    }

    val carParameters = CarParameters
    override fun calculateU_pkw(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val travelTimeDirect = impedance.duration(origin, destination, car, previousActivity.endTime)

        val travelTimeFixed: Duration = person.nextFixedActivity()?.let {
            impedance.duration(destination, it.location, car, previousActivity.endTime)
        } ?: Duration.ZERO

        val travelTime = max(travelTimeFixed, travelTimeDirect)
        val travelCostDirect = impedance.cost(origin, destination, car, previousActivity.endTime)
        val travelCostFixed = person.nextFixedActivity()?.let {
            impedance.cost(destination, it.location, car, previousActivity.endTime)
        } ?: 0.euros
        val travelCost = max(travelCostFixed, travelCostDirect)
        // TODO Engine type influence on cost is currently not implemented as the parameters are 1.0

        return carParameters.evaluate(utilityScope, travelTime, travelCost)
    }

    val passengerParameters = PassengerParameters
    override fun calculateU_mf(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val duration = impedance.duration(
            origin,
            destination,
            passenger,
            previousActivity.endTime
        )
        return passengerParameters.evaluate(utilityScope, duration)
    }

    val publicTransportParameters = PublicTransportParameters
    override fun calculateU_oev(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val durationUnit = DurationUnit.MINUTES

        return publicTransportParameters.alpha.evaluate(utilityScope) +
            publicTransportParameters.travelTimeBeta.evaluate(
                impedance.duration(
                    origin,
                    destination,
                    pt,
                    previousActivity.endTime
                ),
                utilityScope
            ) {
                val duration = if (it <= Duration.ZERO) 1000.minutes else it.coerceAtMost(1000.minutes)
                duration.toDouble(durationUnit)
            } + publicTransportParameters.travelCostBeta.evaluate(
                impedance.cost(
                    origin,
                    destination,
                    pt,
                    previousActivity.endTime
                ),
                utilityScope
            ) {
                it.coerceAtMost(1000.euros).toDouble(CurrencyUnit.EUROS)
            } * (!person.hasCommuterTicket).D
    }

    val bikesharingParameters = BikesharingParameters
    override fun calculateU_bs(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val cost = impedance.cost(
            origin,
            destination,
            bikesharing,
            previousActivity.endTime
        )
        val travelTime = impedance.duration(origin, destination, bikesharing, previousActivity.endTime)
        return bikesharingParameters.evaluate(utilityScope, travelTime, cost)
    }

    val moiaParameters = MoiaParameters
    override fun calculateU_moia(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)

        val membership = person.memberships.getOrDefault("Moia_an_member", false).D * 0.920837012352522
        val membershipCost = person.memberships.getOrDefault("Moia_an_member", false).D * 0.0357690383757927
        val cost = impedance.cost(
            origin,
            destination,
            ridepooling,
            previousActivity.endTime
        )
        val travelTime = impedance.duration(origin, destination, ridepooling, previousActivity.endTime)
        // TODO pflansch this structure with evaluatePlus in the Evaluate interface
        return moiaParameters.alpha.evaluate(utilityScope) + membership +
            moiaParameters.travelTimeBeta.evaluate(travelTime, utilityScope) {
                it.coerceAtMost(1000.minutes).toDouble(DurationUnit.MINUTES)
            } +
            moiaParameters.travelCostBeta.evaluatePlus(cost, utilityScope, membershipCost) {
                it.coerceAtMost(1000.euros).toDouble(CurrencyUnit.EUROS)
            }
    }

    val eScooterParameters = EScooterParameters
    override fun calculateU_escooter(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val cost = impedance.cost(
            origin,
            destination,
            escooter,
            previousActivity.endTime
        )
        val travelTime = impedance.duration(origin, destination, escooter, previousActivity.endTime)
        return eScooterParameters.evaluate(utilityScope, travelTime, cost)
    }

    val csffParameters = CarsharingFreeFloatingParameters
    override fun calculateU_cs_ff(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val cost = impedance.cost(
            origin,
            destination,
            csff,
            previousActivity.endTime
        )
        val travelTime =
            impedance.duration(origin, destination, csff, previousActivity.endTime)
        return csffParameters.evaluate(utilityScope, travelTime, cost)
    }

    val cssbParameters = CarsharingStationParameters
    override fun calculateU_cs_sb(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val cost = impedance.cost(
            origin,
            destination,
            cssb,
            previousActivity.endTime
        )
        val travelTime =
            impedance.duration(origin, destination, cssb, previousActivity.endTime)
        return cssbParameters.evaluate(utilityScope, travelTime, cost)
    }

    val taxiParameters = TaxiParameters
    override fun calculateU_taxi(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)
        val cost = impedance.cost(
            origin,
            destination,
            taxi,
            previousActivity.endTime
        )
        val travelTime =
            impedance.duration(origin, destination, taxi, previousActivity.endTime)
        return taxiParameters.evaluate(utilityScope, travelTime, cost)
    }
}
