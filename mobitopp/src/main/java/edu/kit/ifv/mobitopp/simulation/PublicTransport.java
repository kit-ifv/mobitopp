package edu.kit.ifv.mobitopp.simulation;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.publictransport.TimetableVerifier;

public interface PublicTransport {

	PersonState initialState(PersonState defaultState);

	Optional<Hook> cleanCacheHook();

	PublicTransportData loadData(Supplier<Network> network, SimulationDays simulationDays,
		TimetableVerifier timetableVerifier) throws IOException;

}
