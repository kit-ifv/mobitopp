package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper;
import edu.kit.ifv.mobitopp.visum.VisumPtLine;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class VisumPtLineBuilder {

	private static final String defaultName = "default line";

	private String name;
	private VisumTransportSystem transportSystem;

	public VisumPtLineBuilder() {
		super();
		name = defaultName;
		transportSystem = TransportSystemHelper.dummySystem();
	}

	public VisumPtLine build() {
		return new VisumPtLine(name, transportSystem);
	}

	public VisumPtLineBuilder uses(VisumTransportSystem transportSystem) {
		this.transportSystem = transportSystem;
		return this;
	}

	public VisumPtLineBuilder withName(String name) {
		this.name = name;
		return this;
	}
}
