package usecases.choicemodels.modechoice

import datastructure.StationaryAction
import domain.data.Person
import domain.data.lastTransportMode
import domain.enums.MODEUNKOWN
import domain.enums.Mode
import domain.location.Location
import domain.location.Metrics
import modeling.discreteChoice.D
import units.CurrencyUnit
import units.Distance
import units.euros
import usecases.AttractivenessModel
import usecases.choicemodels.ChoiceModelModes
import usecases.choicemodels.IGeneratedHcUtilityFunction
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
import utils.units.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

data class ModeParameters(
    val modes: ChoiceModelModes,
    val pedestrianParameters: Evaluable = PedestrianParameters,
    val bikeParameters: Evaluable = BikeParameters(modes.bike),
    val carParameters: Evaluable = CarParameters(modes.car),
    val bikesharingParameters: Evaluable = BikesharingParameters,
    val moiaParameters: WithCost = MoiaParameters, // TODO moia has complex calculation in the utility function
    val csffParameters: Evaluable = CarsharingFreeFloatingParameters,
    val cssbParameters: Evaluable = CarsharingStationParameters(modes.carSharingStation),
    val taxiParameters: Evaluable = TaxiParameters,
    val passengerParameters: Evaluable = PassengerParameters(modes.passenger),
    val publicTransportParameters: WithCost = PublicTransportParameters(
        modes.publicTransport
    ), // TODO pt has complex calculation in utility function.
    val eScooterParameters: Evaluable = EScooterParameters,
)

class ModernizedModeUtility(
    override val modes: ChoiceModelModes,
    val attractivenessModel: AttractivenessModel,
    parameters: ModeParameters,
) : IGeneratedHcUtilityFunction {

    lateinit var utilityScope: CombinedScope

    private lateinit var relevant: Relevant

    private data class Relevant(
        val person: Person,
        val origin: Location,
        val destination: Location,
        val previousActivity: StationaryAction,
        val nextActivity: StationaryAction

    )

    // TODO all of these could be appended to their corresponding parameter set
    val taxi = modes.taxi
    val ridepooling = modes.ridePooling
    val bikesharing = modes.bikeSharing
    val csff = modes.carSharingFree
    val cssb = modes.carSharingStation
    val escooter = modes.eScooter
    val car = modes.car
    val passenger = modes.passenger
    val bike = modes.bike
    val pt = modes.publicTransport
    val ped = modes.pedestrian

    private val pedestrianParameters = parameters.pedestrianParameters
    private val bikeParameters = parameters.bikeParameters
    private val carParameters = parameters.carParameters
    private val bikesharingParameters = parameters.bikesharingParameters
    private val moiaParameters = parameters.moiaParameters
    private val csffParameters = parameters.csffParameters
    private val cssbParameters = parameters.cssbParameters
    private val taxiParameters = parameters.taxiParameters
    private val passengerParameters = parameters.passengerParameters
    private val publicTransportParameters = parameters.publicTransportParameters
    private val eScooterParameters = parameters.eScooterParameters

    private fun setUtilityScope(
        person: Person,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        distance: Distance,
        targetZone: Location
    ) {
        utilityScope = CombinedScope(
            ModePersonScope(
                person, nextActivity, person.lastTransportMode(previousActivity) ?: MODEUNKOWN,

            ),

            ModeZoneScope(
                previousActivity.endTime, distance,
                attractivenessModel.parkingPressure(targetZone.requireZone())
            )
        )
    }

    @Suppress("LongParameterList")
    fun updateUtilityScope(
        person: Person,
        origin: Location,
        destination: Location,
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
            impedance.distance(origin, destination, car),
            destination
        )
    }

    override fun calculateU_fuss(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_rad(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_pkw(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_mf(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_oev(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_bs(
        person: Person,
        origin: Location,
        destination: Location,
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

    @Suppress("MagicNumber") // TODO move the constants out of this method, maybe into the parameter object
    override fun calculateU_moia(
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        updateUtilityScope(person, origin, destination, previousActivity, nextActivity, impedance)

        // TODO extract hardcoded string "Moia_an_member"
        val moiaString = "Moia_an_member"
        val membership = person.memberships.any { it.key.name == moiaString }.D * 0.920837012352522
        val membershipCost = person.memberships.any { it.key.name == moiaString }.D * 0.0357690383757927
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

    override fun calculateU_escooter(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_cs_ff(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_cs_sb(
        person: Person,
        origin: Location,
        destination: Location,
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

    override fun calculateU_taxi(
        person: Person,
        origin: Location,
        destination: Location,
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
