package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.util.Collections.singletonMap;

import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;

public class TransportSystemHelper {

	public static VisumTransportSystemSet asSet(VisumTransportSystem system) {
		VisumTransportSystems systems = new VisumTransportSystems(singletonMap(system.code, system));
		return VisumTransportSystemSet.getByCode(system.code, systems);
	}

	public static VisumTransportSystemSet dummySet() {
		return asSet(dummySystem());
	}

	public static VisumTransportSystem dummySystem() {
		final String systemName = "dummy system";
		return transportSystemWith(systemName);
	}

	private static VisumTransportSystem transportSystemWith(final String systemName) {
		return new VisumTransportSystem(systemName, systemName, systemName);
	}

	public static VisumTransportSystems dummySystems() {
		VisumTransportSystem system = dummySystem();
		return new VisumTransportSystems(singletonMap(system.code, system));
	}

	public static VisumTransportSystemSet otherSet() {
		return asSet(transportSystemWith("other name"));
	}

}
