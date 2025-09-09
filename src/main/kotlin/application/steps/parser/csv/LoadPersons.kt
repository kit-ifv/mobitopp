package application.steps.parser.csv

import core.modelsteps.AddResourceStep
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
import domain.synthesis.parser.personCsvParser
import units.CurrencyUnit
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.withFilter
import java.nio.file.Path

@Suppress("LongParameterList", "UnusedParameter")
fun LoadPersonsContext.preparePersons(
    path: Path = defaultPersonPath,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: PersonColumns = PersonColumns(),
    incomeUnit: CurrencyUnit = costUnit,
    filter: PersonColumns.(Row, LoadPersonsContext) -> Boolean = { _, _ -> true },
    addResourceStep: AddResourceStep<MutablePerson, PersonId> = csvResourceStep(path, columns, errorHandling, incomeUnit, filter, delimiter),
) {

    this.preparePersonsFile(addResourceStep)
}

fun LoadPersonsContext.csvResourceStep(
    path: Path,
    columns: PersonColumns,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    incomeUnit: CurrencyUnit = costUnit,
    filter: PersonColumns.(Row, LoadPersonsContext) -> Boolean = { _, _ -> true },
    delimiter: String = SEMICOLON,
): AddResourceStep<MutablePerson, PersonId> {
    val providersByNameFunction: () -> Map<String, SharingProvider> = {
        sharingProviderRepository.elements.associateBy { it.name.lowercase() }
    }
    val csvParser = personCsvParser(errorHandling, columns, incomeUnit, providersByNameFunction) {
        getHousehold(it)
    }

    val internalFilter = { row: Row -> columns.filter(row, this) }
    return LoadCsvStep<MutablePerson, PersonId>(
        path = path,
        name = "Load Person from csv",
        parser = csvParser.withFilter(internalFilter),
        delimiter = delimiter,
        repository = personRepository,
        dependentRepositories = setOf(householdRepository, sharingProviderRepository),
        validationMock = listOf() // TODO
    )
}

fun LoadPersonsContext.preparePersonsFile(

    addResourceStep: AddResourceStep<MutablePerson, PersonId>,
) = runStep {
    addResourceStep
}

fun LoadPersonsContext.finishPersons() = runStep {
    SealStep(personRepository)
}

fun LoadPersonsContext.loadPersons(
    path: Path = defaultPersonPath,
    filter: PersonColumns.(Row, LoadPersonsContext) -> Boolean = { _, _ -> true },
) {
    this.preparePersons(path = path, filter = filter)
    this.finishPersons()
}

interface LoadPersonsContext : DemandSimContext, PersonCsvContext {
    val personRepository: MutableRepository<MutablePerson, PersonId>
    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>

    val defaultPersonPath: Path
        get() = dataFolder.resolve("demand-data").resolve("person.csv")

    fun getHousehold(
        householdId: HouseholdId,
    ) = requireNotNull(
        householdRepository[householdId]
    ) {
        "Referenced household id $householdId could not be found in householdRepo:" +
                " ${householdRepository.elements.map { it.id }.toList()}"
    }
}
