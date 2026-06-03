package application.steps.model

import application.steps.HasCarRepo
import application.steps.HasDrtProviderAgentRepo
import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonAgentRepo
import application.steps.HasPersonBehavior
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderAgentRepo
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import application.steps.SimulationConfig
import core.modelsteps.Config
import core.modelsteps.resources.LazyResource
import core.modelsteps.steps.addResourceStep
import core.modelsteps.steps.seal
import core.statemachine.StateMachineFactory
import domain.shared.location.zone.Zone
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.DrtAlgorithm
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.behavior.ActivityDurationRandomizer
import domain.simulation.behavior.GaussianActivityDurationRandomizer
import domain.simulation.behavior.NoDurationRandomizer
import domain.simulation.data.DrtProvider
import domain.simulation.data.SharingProvider
import domain.simulation.data.car.PrivateCar
import domain.simulation.data.household.Household
import domain.simulation.data.person.Person

/**
 * Creates a [GaussianActivityDurationRandomizer] based on the simulation duration.
 *
 * @param config The simulation configuration containing start and end times. Provided via context.
 * @return A [GaussianActivityDurationRandomizer] with a maximum duration equal to the simulation length.
 */
context(config: SimulationConfig)
fun gaussianDurationRandomizer() = GaussianActivityDurationRandomizer(
    max = config.simulationEnd.minus(config.simulationStart),
)

/**
 * Builds simulation agents for persons, DRT providers, and sharing providers.
 *
 * This step initializes the agents required for the simulation. It uses the provided state machine factories
 * and algorithms to create [PersonAgent], [DrtProviderAgent], and [SharingProviderAgent] instances from
 * the synthesized data.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasPersonRepo] for [domain.simulation.data.person.Person]
 *   - [HasZoneRepo] for [Zone]
 *   - [HasHouseholdRepo] for [domain.simulation.data.household.Household]
 *   - [HasCarRepo] for [PrivateCar]
 *   - [HasSharingProviderRepo] for [domain.simulation.data.SharingProvider]
 *   - [HasDrtProviderRepo] for [domain.simulation.data.DrtProvider]
 *   - [HasPersonAgentRepo] for [PersonAgent]
 *   - [HasSharingProviderAgentRepo] for [SharingProviderAgent]
 *   - [HasDrtProviderAgentRepo] for [DrtProviderAgent]
 *   - [HasPersonBehavior]
 * @param config The general configuration. Provided via context. Must implement [Config].
 * @param personStateMachine Factory to create state machines for [PersonAgent]s.
 * @param durationRandomizer Randomizer for activity durations. Defaults to [NoDurationRandomizer].
 * @param drtStateMachine Optional factory to create state machines for [DrtProviderAgent]s.
 * @param drtAlgorithm Optional function to create [DrtAlgorithm]s for [domain.simulation.data.DrtProvider]s.
 */
context(config: Config)
fun <C> C.buildSimulationAgents(
    personStateMachine: StateMachineFactory<PersonAgent>,
    durationRandomizer: ActivityDurationRandomizer = NoDurationRandomizer,
    drtStateMachine: StateMachineFactory<DrtProviderAgent>? = null,
    drtAlgorithm: ((DrtProvider) -> DrtAlgorithm)? = null,
)
    where C : HasPersonRepo<*, Person>,
          C : HasZoneRepo<*, Zone<*>>,
          C : HasHouseholdRepo<*, Household>,
          C : HasCarRepo<*, PrivateCar>,
          C : HasSharingProviderRepo<*, SharingProvider>,
          C : HasDrtProviderRepo<*, DrtProvider>,
          C : HasPersonAgentRepo<PersonAgent, *>,
          C : HasSharingProviderAgentRepo<SharingProviderAgent, *>,
          C : HasDrtProviderAgentRepo<DrtProviderAgent, *>,
          C : HasPersonBehavior {
    val builder by lazy {
        BuildAgents(
            seed = config.seed,
            personStateMachine,
            personBehavior,
            drtStateMachine,
            drtAlgorithm,
            durationRandomizer,
        )
    }

    val sourceName = "buildSimulationAgents"
    addResourceStep(
        name = "build sharing provider agents",
        resource = LazyResource("sharing provider agents by BuildAgents", sourceName) {
            builder.buildSharingProviderAgents(sharingProviderRepository.elements.toList()).asSequence()
        },
        repository = mutableSharingProviderAgentRepository,
        dependentRepositories = setOf(sharingProviderRepository, zoneRepository),
    )

    seal(mutableSharingProviderAgentRepository)

    addResourceStep(
        name = "build drt provider agents",
        resource = LazyResource("drt provider agents by BuildAgents", sourceName) {
            builder.buildDrtProviderAgents(drtProviderRepository.elements.toList()).asSequence()
        },
        repository = mutableDrtProviderAgentRepository,
        dependentRepositories = setOf(drtProviderRepository, zoneRepository),
    )

    seal(mutableDrtProviderAgentRepository)

    addResourceStep(
        name = "build person agents",
        resource = LazyResource("person agents by BuildAgents", sourceName) {
            builder.buildPersonAgents(householdRepository.elements.toList()).asSequence()
        },
        repository = mutablePersonAgentRepository,
        dependentRepositories = setOf(
            personRepository,
            householdRepository,
            carRepository,
            zoneRepository,
            sharingProviderRepository,
            drtProviderRepository,
        ),
    )

    seal(mutablePersonAgentRepository)
}
