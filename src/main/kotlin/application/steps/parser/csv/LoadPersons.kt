package application.steps.parser.csv

import core.modelsteps.AbstractAddResourceStep
import core.modelsteps.FileBasedAddResourceStep
import core.modelsteps.GroupedStepBuilder
import core.modelsteps.LoadCsvStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.SealStep
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.PersonId
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.parser.PersonColumns
import domain.synthesis.parser.PersonCsvContext
import domain.synthesis.parser.binary.BinaryPersonReader
import domain.synthesis.parser.binary.BinaryPersonWriter
import domain.synthesis.parser.personCsvParser
import edu.kit.ifv.units.CurrencyUnit
import utils.ErrorHandling
import utils.binary.BinaryWriter
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.long
import utils.csv.withFilter
import java.nio.file.Path

@Suppress("LongParameterList", "UnusedParameter")
fun LoadPersonsContext.preparePersons(
    path: Path = defaultPersonPath,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: PersonColumns = PersonColumns(),
    incomeUnit: CurrencyUnit = costUnit,
    addResourceStep: AbstractAddResourceStep<MutablePerson, PersonId> =
        personsFromCsvStep(path) {
            this.delimiter = delimiter
            this.errorHandling = errorHandling
            this.columns = columns
            this.incomeUnit = incomeUnit
        }.step,
) {
    this.preparePersonsFile(addResourceStep)
}

data class PersonCsvConfig(
    var path: Path,
    var columns: PersonColumns = PersonColumns(),
    var delimiter: String = SEMICOLON,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    var incomeUnit: CurrencyUnit,

) {

    /* Filtering should not be done on a resource step but afterward. For performance it doesnt matter because CSV
    parser remains slow regardless of filter. And Resource wrapping is nigh impossible because each resource step could
    define its own filtering logic that doesnt share any similarity with any other step.
     */
    val filter: PersonColumns.(Row, LoadPersonsContext) -> Boolean = { row, context ->

        HouseholdId(row.long(this.householdColumn)) in context.householdRepository
    }
}

/**
 * Collects the instructions for initializing the person repository. Automatically sets the reader and writer
 * to the proper binary implementations.
 */
class PersonStepBuilder(val seed: Long, val converter: (HouseholdId) -> MutableHousehold?) :
    GroupedStepBuilder<MutablePerson, PersonId>() {
    override val reader = BinaryPersonReader(converter, seed)
    override val writer: BinaryWriter<MutablePerson> = BinaryPersonWriter()

//    override fun fromCSV(
//        source: Path,
//        lambda: context(Path) () -> AbstractAddResourceStep<MutablePerson, PersonId>,
//    ): FileBasedAddResourceStep<MutablePerson, PersonId> {
//        return context(source) {
//            FileBasedAddResourceStep(source, lambda(source))
//        }
//    }
}

/**
 * Operate on the context object. Apply the steps defined in the builder and then finalize the repository.
 */
fun LoadPersonsContext.persons(lambda: PersonStepBuilder.() -> Unit) {
    val lpcBuilder = PersonStepBuilder(this.simulationSeed, householdRepository::get)
    lambda(lpcBuilder)
    lpcBuilder.executeOn(this)
    finishPersons()
}

fun LoadPersonsContext.personsFromCsvStep(
    path: Path = defaultPersonPath,
    lambda: PersonCsvConfig.() -> Unit,
): FileBasedAddResourceStep<MutablePerson, PersonId> {
    val config = PersonCsvConfig(path = path, incomeUnit = costUnit)
    config.apply(lambda)
    return config.run {
        val providersByNameFunction: () -> Map<String, SharingProvider> = {
            sharingProviderRepository.elements.associateBy { it.name.lowercase() }
        }
        val csvParser = personCsvParser(errorHandling, columns, incomeUnit, providersByNameFunction) {
            getHousehold(it)
        }

        val internalFilter = { row: Row -> columns.filter(row, this@personsFromCsvStep) }
        val step = LoadCsvStep<MutablePerson, PersonId>(
            path = path,
            name = "Load Person from csv",
            parser = csvParser.withFilter(internalFilter),
            delimiter = delimiter,
            repository = personRepository,
            dependentRepositories = setOf(householdRepository, sharingProviderRepository),
            validationMock = listOf() // TODO
        )
        FileBasedAddResourceStep(path, step)
    }
}

fun LoadPersonsContext.preparePersonsFile(

    addResourceStep: AbstractAddResourceStep<MutablePerson, PersonId>,
) = runStep {
    addResourceStep
}

fun LoadPersonsContext.finishPersons() = runStep {
    SealStep(personRepository)
}

fun LoadPersonsContext.loadPersons(
    path: Path = defaultPersonPath,
) {
    this.preparePersons(path = path)
    this.finishPersons()
}

interface LoadPersonsContext : DemandSimContext, PersonCsvContext {
    val personRepository: MutableRepository<MutablePerson, PersonId>
    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>

    val defaultPersonPath: Path
        get() = dataFolder.resolve("person.csv")

    fun getHousehold(
        householdId: HouseholdId,
    ) = requireNotNull(
        householdRepository[householdId]
    ) {
        "Referenced household id $householdId could not be found in householdRepo:" +
            " ${householdRepository.elements.map { it.id }.toList()}"
    }
}
