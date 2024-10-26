package usecases.models

import domain.data.Person
import domain.enums.Mode
import domain.location.LOCATIONUNKNOWN
import domain.location.Metrics
import modeling.models.LogitModel
import units.CurrencyUnit
import units.DistanceUnit
import utils.units.Time
import kotlin.time.DurationUnit

@Suppress("LongParameterList")
class ModeChoiceModel(
    override val name: String,
    val car: Mode,
    val bike: Mode,
    val metrics: Metrics,
    val bCost: Double = 1.0,
    val bDist: Double = 1.0,
    val bDur: Double = 1.0,
) : LogitModel<Person, Mode>() {

    override fun utility(agent: Person, choice: Mode, time: Time): Double {
        val from = LOCATIONUNKNOWN
        val to = LOCATIONUNKNOWN

        return (
            asc(choice) +
                bCost * metrics.cost(from, to, choice, time).toDouble(CurrencyUnit.EUROS) +
                bDist * metrics.distance(from, to, choice).toDouble(DistanceUnit.METERS) +
                bDur * metrics.duration(from, to, choice, time).toDouble(DurationUnit.MINUTES)
            )
    }

    @Suppress("MagicNumber")
    private fun asc(mode: Mode): Double = when (mode) {
        car -> 2.0
        bike -> -0.5
        else -> 0.0
    }

    override fun choices(agent: Person, time: Time): Set<Mode> = setOf(bike, car)
}
