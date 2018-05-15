package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

interface PassengerResults {

	void board(
			SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip);

	void getOff(
			SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip);

	void wait(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip);

	void vehicleFull(SimulationPerson person, Time time, PublicTransportTrip trip);

}
