package application.steps.model

import application.steps.HasPersonRepo
import core.modelsteps.resources.MutableRepository
import core.modelsteps.scopes.transformEachStep
import domain.simulation.cars.AssignCarUserModel
import domain.synthesis.data.car.CarId
import domain.synthesis.data.car.MutablePrivateCar
import domain.synthesis.data.person.Person

/**
 * Assigns main car users to private cars.
 *
 * This step iterates over all private cars in the [repository] and uses the provided [model]
 * to assign a person as the main user of the car.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] for [Person].
 * @param repository The mutable repository of private cars to update. Provided via context.
 * @param model The model used to determine and assign the main user. Defaults to [AssignCarUserModel].
 */
context(repository: MutableRepository<MutablePrivateCar, CarId>)
fun <C> C.assignMainCarUsers(
    model: AssignCarUserModel = AssignCarUserModel(), // TODO create interface CarUserAssignment
) where C : HasPersonRepo<*, Person> {
    transformEachStep( // TODO maybe request ordering on elements for this transformation?
        name = "Assign cars to household members as main users.",
        dependentRepositories = setOf(personRepository),
    ) { person ->
        model.assign(person)
    }
}
