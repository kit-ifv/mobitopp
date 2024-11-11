package usecases.choicemodels.modechoice

/**
 * Mode choice usually requires parameters originating from a person (age, employment, number of cars) and a zone
 * (distance, attractivity, parking pressure). This interface combines both [PersonModeParameters] and [ZoneModeParameters]
 * together
 *
 * @property constant a constant factor that should be applied to any parameter block evaluation.
 */
interface CombinedModeParameters : PersonModeParameters, ZoneModeParameters {
    override val constant: Double
    fun evaluate(
        scope: CombinedScope
    ) = constant + toParameters(scope.personScope).toDouble() + toParameters(scope.zoneScope).toDouble()
}

/**
 * Encapsulates a [ModePersonScope] and [ModeZoneScope] in one class.
 *
 * @property personScope the target [ModePersonScope]
 * @property zoneScope the target [ModeZoneScope]
 */
data class CombinedScope(
    val personScope: ModePersonScope,
    val zoneScope: ModeZoneScope
)
