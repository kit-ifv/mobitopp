package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.PublicTransport;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

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
	public PublicTransportData loadData(VisumNetwork network, SimulationDays simulationDays) {
		return PublicTransportData.noAssignement;
	}

}
