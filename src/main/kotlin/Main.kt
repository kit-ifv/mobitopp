
import domain.data.EconomicStatus
import domain.enums.Bbsr17
import modeling.steps.Run
import usecases.LegacyContext
import usecases.assignCarUsers
import usecases.finishActivities
import usecases.legacyData.finishPrivateCars
import usecases.legacyData.loadHouseholds
import usecases.legacyData.loadZones
import usecases.legacyData.preparePrivateCars
import usecases.loadPersons
import usecases.prepareActivities
import utils.ErrorHandling
import java.io.File

fun main() {
    Run {
        LegacyContext(
            scenarioName = "testSteps",
            areaTypeCodes = Bbsr17,
            demandFolder = File(
                // "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output" +
                // "\\logiktram_karlsruhe_long-term-module\\karlsruhe"
                "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output\\" +
                    "logiktram_rastatt_long-term-module\\rastatt"
                // "D:\\gitlab\\logiktram\\output\\rastatt"
            ),
            economicalStatusCodes = EconomicStatus
        )
    }.steps {
        loadZones()
        loadHouseholds()
        loadPersons()
        preparePrivateCars() // file = File("example/car.csv"))
        assignCarUsers()
        finishPrivateCars()
        prepareActivities(errorHandling = ErrorHandling.WARNING)
        finishActivities()
    }
}

/*
typealias HouseholdId = ID<Household>
typealias PersonId = ID<Person>

interface Household : Identifiable<HouseholdId> {
    val garden: Area
    val members: List<Person>

    fun addMember(member: Person)
}

interface Person : Identifiable<PersonId> {
    val household: Household
    val income: Currency
    val age: Int
}

interface Tourist : Person {
    val overNight: Boolean
}

interface HouseholdManager {
    val householdRepo: RepositoryBuilder<HouseholdBuilder, Household, HouseholdId>
    val defaultAreaUnit: AreaUnit
}

interface PersonManager {
    val personRepo: RepositoryBuilder<PersonBuilder, Person, PersonId>
    val defaultCurrencyUnit: CurrencyUnit
}

interface TouristManager : PersonManager {
    val touristRepo: RepositoryBuilder<TouristBuilder, Tourist, PersonId>
}

data class HouseholdBuilder(
    var id: Long? = null,
    var garden: Area? = null,
) : Builder<Household> {

    override fun build() = object : Household {
        override val garden = this@HouseholdBuilder.garden!!
        override val id = HouseholdId(this@HouseholdBuilder.id!!)
        override val members: List<Person>
            get() = internalMembers

        private val internalMembers = mutableListOf<Person>()

        override fun addMember(member: Person) {
            check(member.household == this)
            internalMembers.add(member)
        }
    }
}

data class PersonBuilder(
    var household: Household? = null,
    var income: Currency? = null,
    var age: Int? = null,
) : Builder<Person> {

    override fun build() = object : Person {
        override val age = this@PersonBuilder.age!!
        override val income = this@PersonBuilder.income!!
        override val household = this@PersonBuilder.household!!
        override val id = drawId()
        init {
            this.household.addMember(this)
        }
    }
}

data class TouristBuilder(
    var household: Household? = null,
    var income: Currency? = null,
    var age: Int? = null,
    var overNight: Boolean? = null,
) : Builder<Tourist> {
    override fun build() = object : Tourist {
        override val age = this@TouristBuilder.age!!
        override val income = this@TouristBuilder.income!!
        override val household = this@TouristBuilder.household!!
        override val overNight = this@TouristBuilder.overNight!!

        override val id = drawId()
        init {
            this.household.addMember(this)
        }
    }
}

@Suppress("LongParameterList")
fun <C> C.parsePerson(
    file: File?,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    ageColumn: String = "age",
    incomeColumn: String = "income",
    householdColumn: String = "householdId",
    currencyUnit: CurrencyUnit?,
) where C : HouseholdManager, C : PersonManager {
    val currency = currencyUnit ?: this.defaultCurrencyUnit

    val parser = CsvParser<PersonBuilder>(errorHandling) { row ->
        PersonBuilder(
            age = row.int(ageColumn),
            income = row.currency(incomeColumn, currency),
            household = householdRepo.getById(row.id(householdColumn))
        )
    }

    val csvPath = file ?: File("some/default/path/person.csv")

    val resource = CsvResource(
        file = csvPath,
        parser = parser,
        delimiter = delimiter
    )

    AddCsvStep(
        name = "read person csv",
        csv = resource,
        repository = this.personRepo
    )
    // ...
}

data class ProjectContext(
    override val scenarioName: String,
    override val demandFolder: File,
    override val defaultAreaUnit: AreaUnit,
    override val defaultCurrencyUnit: CurrencyUnit,
) : Context, HouseholdManager, PersonManager {

    override val householdRepo = RepositoryBuilder<HouseholdBuilder, Household, HouseholdId>()
    override val personRepo = RepositoryBuilder<PersonBuilder, Person, PersonId>()

    override fun reset() {
        householdRepo.reset()
        personRepo.reset()
    }
}
*/
