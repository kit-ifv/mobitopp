package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

interface PassengerLogger {

	void board(SimulationPerson person, Time time, PublicTransportLeg part);

	void getOff(SimulationPerson person, Time time, PublicTransportLeg part);

	void wait(SimulationPerson person, Time time, PublicTransportLeg part);

	void vehicleFull(SimulationPerson person, Time time, PublicTransportTrip trip);

}
