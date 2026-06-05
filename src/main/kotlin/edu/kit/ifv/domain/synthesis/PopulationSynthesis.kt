package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.results.OpportunityOutput
import java.nio.file.Path
/**
 * Configures and executes a population synthesis.
 *
 * The class stores the immutable inputs required to create [SynthesisSteps]:
 * [zones], [surveyHouseholds], and the configured output directory.
 *
 * @param AREA area type used by the synthesis steps.
 * @param S household attribute type.
 * @param T person attribute type.
 * @property zones available synthesis areas.
 * @property surveyHouseholds survey households used by the synthesis.
 */
class PopulationSynthesis<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    private val outputDirectory: Path,
    val zones: List<AREA>,
    val surveyHouseholds: Collection<ISurveyHousehold<S, T>>,
) {

    val opportunities: MutableList<OpportunityOutput> = mutableListOf()
    fun execute(lambda: SynthesisSteps<AREA, S, T>.() -> Unit) {
        SynthesisSteps(
            zones,
            surveyHouseholds,
            outputDirectory,
        ).apply(lambda)
    }

    companion object {

        /**
         * Mutable configuration used by [configure].
         *
         * The survey population is initialized from [surveyPopulationGenerator] and may be
         * replaced before [configure] creates the [PopulationSynthesis]. [outputDirectory]
         * must be initialized by the configuration block.
         *
         * @param AREA area type used by the synthesis.
         * @param S household attribute type.
         * @param T person attribute type.
         * @param surveyPopulationGenerator generator used to create the initial survey population.
         */
        class SynthesisConfiguration<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
            surveyPopulationGenerator: GenerateSurveyHouseholds<S, T>,
        ) {
            var surveyPopulation = surveyPopulationGenerator.generateSurveyHouseholds()
            lateinit var outputDirectory: Path
        }

        /**
         * Creates a [PopulationSynthesis] from a generated survey population, [zones], and
         * a mutable configuration block.
         *
         * @param AREA area type used by the synthesis.
         * @param S household attribute type.
         * @param T person attribute type.
         * @param surveyPopulation generator used to create the initial survey population.
         * @param zones available synthesis areas.
         * @param lambda configuration block applied before creating the synthesis.
         * @return a configured [PopulationSynthesis].
         */
        fun <AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> configure(
            surveyPopulation: GenerateSurveyHouseholds<S, T>,
            zones: List<AREA>,
            lambda: SynthesisConfiguration<AREA, S, T>.() -> Unit,
        ): PopulationSynthesis<AREA, S, T> {
            val config = SynthesisConfiguration<AREA, S, T>(surveyPopulation).apply(lambda)

            return PopulationSynthesis(
                config.outputDirectory,
                zones,
                config.surveyPopulation,
            )
        }
    }
}
