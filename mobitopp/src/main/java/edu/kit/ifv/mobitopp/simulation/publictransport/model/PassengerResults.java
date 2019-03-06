package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

interface PassengerResults {

	void board(SimulationPerson person, Time time, PublicTransportLeg part, Trip trip);

	void getOff(SimulationPerson person, Time time, PublicTransportLeg part, Trip trip);

	void wait(SimulationPerson person, Time time, PublicTransportLeg part, Trip trip);

	void searchNewTrip(SimulationPerson person, Time time, PublicTransportTrip trip);

}
