package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.DemandSimulatorPassenger;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPersonPassenger;

public class DemandSimulatorTour 
	extends DemandSimulatorPassenger {
	
	public DemandSimulatorTour(
		final DestinationChoiceModel destinationChoiceModel,
		final TourBasedModeChoiceModel modeChoiceModel,
		final ZoneBasedRouteChoice routeChoice,
    final ActivityStartAndDurationRandomizer activityDurationRandomizer,
    final ReschedulingStrategy rescheduling,
    final Set<Mode> modesInSimulation,
    final PersonState initialState, 
    final SimulationContext context
  ) {
		super(destinationChoiceModel,modeChoiceModel,routeChoice,activityDurationRandomizer,rescheduling,
				modesInSimulation,initialState,context);
  }
	
  public DemandSimulatorTour(
      final DestinationChoiceModel destinationChoiceModelForDemandSimulation_,
			final TourBasedModeChoiceModel modeChoice,
			final ZoneBasedRouteChoice routeChoice,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer,
      final ReschedulingStrategy rescheduling,
			final PersonState initialState,
			final SimulationContext context
	)
  {
		this(destinationChoiceModelForDemandSimulation_,
					modeChoice,
					routeChoice,
					activityDurationRandomizer,
					rescheduling,
					Mode.CHOICE_SET_FULL,
					initialState, 
					context
			);
	}

	protected SimulationPersonPassenger createSimulatedPerson(
			EventQueue queue, PublicTransportBehaviour boarder, long seed, Person p,
			PersonResults results) {
		return new SimulationPersonTour(p, 
																					zoneRepository(),
																					queue,
																					simulationOptions(), 
																					simulationDays(),
																					this.modesInSimulation,
																					tourFactory, this.initialState,
																					boarder,
																					seed,
																					results
																				);
	}

}
