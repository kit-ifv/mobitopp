package edu.kit.ifv.mobitopp.simulation.events;

import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public interface DemandSimulationEventIfc {
  
	SimulationPerson getPerson();
  OccupationIfc getOccupation();
  Time getSimulationDate();

	int getPriority();
	void writeRemaining(PersonResults results);
}
