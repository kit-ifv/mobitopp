package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.DemandSimulatorPassenger;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.DefaultTripFactory;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPersonPassenger;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;

public class DemandSimulatorTour 
	extends DemandSimulatorPassenger {
	
	public DemandSimulatorTour(
		final DestinationChoiceModel destinationChoiceModel,
		final TourBasedModeChoiceModel modeChoiceModel,
		final ZoneBasedRouteChoice routeChoice,
    final ActivityStartAndDurationRandomizer activityDurationRandomizer,
    final TripFactory tripFactory,
    final ReschedulingStrategy rescheduling,
    final Set<Mode> modesInSimulation,
    final PersonState initialState, 
    final SimulationContext context
  ) {
    super(destinationChoiceModel, modeChoiceModel, routeChoice, activityDurationRandomizer,
        tripFactory, rescheduling, modesInSimulation, initialState, context);
  }
	
  public DemandSimulatorTour(
      final DestinationChoiceModel destinationChoiceModelForDemandSimulation_,
			final TourBasedModeChoiceModel modeChoice,
			final ZoneBasedRouteChoice routeChoice,
			final ActivityStartAndDurationRandomizer activityDurationRandomizer,
      final TripFactory tripFactory,
      final ReschedulingStrategy rescheduling,
			final PersonState initialState,
			final SimulationContext context
	)
  {
		this(destinationChoiceModelForDemandSimulation_,
					modeChoice,
					routeChoice,
					activityDurationRandomizer,
					tripFactory, 
					rescheduling,
					Mode.CHOICE_SET_FULL,
					initialState, 
					context
			);
	}
 
  
	@Override
	protected SimulationPersonPassenger createSimulatedPerson(
			EventQueue queue, PublicTransportBehaviour boarder, long seed, Person p,
			PersonListener listener, Set<Mode> modesInSimulation, PersonState initialState) {
		TripFactory tripFactory = new DefaultTripFactory();
    return new SimulationPersonTour(p, 
																					zoneRepository(),
																					queue,
																					simulationOptions(), 
																					simulationDays(),
																					modesInSimulation,
																					tourFactory, 
																					tripFactory,
																					this.initialState,
																					boarder,
																					seed,
																					listener
																				);
	}

}
