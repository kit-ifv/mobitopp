package edu.kit.ifv.application.steps.model
import edu.kit.ifv.application.steps.HasCarRepo
import edu.kit.ifv.application.steps.HasDrtProviderAgentRepo
import edu.kit.ifv.application.steps.HasDrtProviderRepo
import edu.kit.ifv.application.steps.HasHouseholdRepo
import edu.kit.ifv.application.steps.HasPersonAgentRepo
import edu.kit.ifv.application.steps.HasPersonRepo
import edu.kit.ifv.application.steps.HasSharingProviderAgentRepo
import edu.kit.ifv.application.steps.HasSharingProviderRepo
import edu.kit.ifv.application.steps.HasZoneRepo
import edu.kit.ifv.application.steps.SimulationConfig
import edu.kit.ifv.core.modelsteps.Config
import edu.kit.ifv.core.modelsteps.resources.LazyResource
import edu.kit.ifv.core.modelsteps.steps.addResourceStep
import edu.kit.ifv.core.modelsteps.steps.seal
import edu.kit.ifv.core.statemachine.StateMachineFactory
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.simulation.agent.BuildAgents
import edu.kit.ifv.domain.simulation.agent.DrtAlgorithm
import edu.kit.ifv.domain.simulation.agent.DrtProviderAgent
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.SharingProviderAgent
import edu.kit.ifv.domain.simulation.behavior.ActivityDurationRandomizer
import edu.kit.ifv.domain.simulation.behavior.GaussianActivityDurationRandomizer
import edu.kit.ifv.domain.simulation.behavior.NoDurationRandomizer
import edu.kit.ifv.domain.simulation.data.car.PrivateCar
import edu.kit.ifv.domain.simulation.data.drt.DrtProvider
import edu.kit.ifv.domain.simulation.data.household.Household
import edu.kit.ifv.domain.simulation.data.person.Person
import edu.kit.ifv.domain.simulation.data.sharing.SharingProvider

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
 *   - [HasSharingProviderRepo] for [SharingProvider]
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
          C : HasDrtProviderAgentRepo<DrtProviderAgent, *>
{
    val builder by lazy {
        BuildAgents(
            seed = config.seed,
            personStateMachine,
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
