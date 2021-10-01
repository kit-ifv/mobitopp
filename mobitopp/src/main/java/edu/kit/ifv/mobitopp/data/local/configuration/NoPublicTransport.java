package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Optional;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.PublicTransport;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.publictransport.TimetableVerifier;

public class NoPublicTransport implements PublicTransport {

	public NoPublicTransport() {
		super();
	}

	/**
	 * SnakeYaml needs a single argument constructor for classes without attributes.
	 * @param dummy  
	 */
	public NoPublicTransport(String dummy) {
		super();
	}

	@Override
	public PersonState initialState(PersonState defaultState) {
		return defaultState;
	}

	@Override
	public Optional<Hook> cleanCacheHook() {
		return Optional.empty();
	}

	@Override
	public PublicTransportData loadData(Supplier<Network> network, SimulationDays simulationDays, TimetableVerifier timetableVerifier) {
		return PublicTransportData.noAssignement;
	}

}
