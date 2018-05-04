package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public interface PublicTransport {

	PersonState initialState(PersonState defaultState);

	Optional<Hook> cleanCacheHook();

	PublicTransportData loadData(VisumNetwork network, SimulationDays simulationDays);

}
