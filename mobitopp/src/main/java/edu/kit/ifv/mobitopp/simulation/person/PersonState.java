package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface PersonState {

	public boolean instantaneous();

	public PersonState nextState(SimulationPerson person, Time currentTime);

	public void doActionAtStart(SimulationPerson person, Time currentTime);
	public void doActionAtEnd(SimulationPerson person, Time currentTime);

	public Optional<DemandSimulationEventIfc> nextEvent(SimulationPerson person, Time currentDate);


}
