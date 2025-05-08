package usecases.steps
//
// import domain.data.Person
// import domain.data.PersonId
// import modeling.steps.ForEachStep
// import modeling.steps.Repository
// import modeling.validation.Warning
//
// fun LoadPersonsContext.applyHomeLocationsInSchedule() = runStep {
//    ApplyHomeLocationInScheduleStep(this)
// }
//
// class ApplyHomeLocationInScheduleStep(
//    context: LoadPersonsContext,
// ) : ForEachStep<Person, PersonId>() {
//    override val name: String = "Assign home location in schedule"
//    override val repository: Repository<Person, PersonId> = context.personRepository
//    private val home = context.homeActivityType
//
//    override val dependentRepositories: Set<Repository<*, *>> = setOf(
//        context.householdRepository,
//    )
//
//    override fun process(element: Person) {
//        element.schedule.activities().filter { act ->
//            act.type == home
//        }.forEach { home ->
//            home.location = element.household.location
//        }
//    }
//
//    override fun verifyInput(): Warning? = null // TODO what can be verified here?
// }
// //
