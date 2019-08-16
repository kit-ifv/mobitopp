package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.SimulationOptions;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public interface SimulationPersonFactory {

	SimulationPerson create(
			Person person, EventQueue queue, SimulationOptions simulationOptions, List<Time> simulationDays,
			Set<Mode> modesInSimulation, TourFactory tourFactory, TripFactory tripFactory,
			PersonState initialState, PublicTransportBehaviour boarder, long seed,
			PersonListener listener);

}
