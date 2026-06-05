package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.shared.behavior.AttractivenessFromCsv
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.ChoiceModelPurposes
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.results.OpportunityOutput
import java.nio.file.Path

class PopulationSynthesis<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    private val outputDirectory: Path,
    val zones: List<AREA>,
    val surveyHouseholds: Collection<ISurveyHousehold<S, T>>,
    val attractivenessModel: AttractivenessModel,
) {

    val opportunities: MutableList<OpportunityOutput> = mutableListOf()
    fun execute(lambda: SynthesisSteps<AREA, S, T>.() -> Unit) {
        SynthesisSteps(
            zones,
            surveyHouseholds,
            attractivenessModel,
            outputDirectory,
            opportunities,
        ).apply(lambda)
    }

    companion object {
        class SynthesisConfiguration<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
            surveyPopulationGenerator: GenerateSurveyHouseholds<S, T>,
        ) {
            var surveyPopulation = surveyPopulationGenerator.generateSurveyHouseholds()
            lateinit var outputDirectory: Path
            lateinit var attractivenessModel: AttractivenessModel

            inner class AttractivenessModelParser {

                lateinit var path: Path // = attractivenessModelPath

                lateinit var purposes: ChoiceModelPurposes
                fun build(): AttractivenessModel = AttractivenessFromCsv(
                    path = path,
                    work = purposes.work,
                    privateVisit = purposes.privateVisit,
                    activityTypes = purposes.allActivityTypes,
                )
            }

            fun attractivenessFromFile(lambda: AttractivenessModelParser.() -> Unit): AttractivenessModel {
                val attractivenessModel = AttractivenessModelParser()
                attractivenessModel.lambda()
                return attractivenessModel.build()
            }
        }

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
                config.attractivenessModel,
            )
        }
    }
}
