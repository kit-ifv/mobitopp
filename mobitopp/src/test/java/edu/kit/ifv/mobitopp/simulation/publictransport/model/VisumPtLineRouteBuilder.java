package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLine;

import edu.kit.ifv.mobitopp.visum.VisumPtLine;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteDirection;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class VisumPtLineRouteBuilder {

	private static final VisumPtLine defaultLine = visumLine().build();
	private static final String defaultName = "default line route";
	private static final String defaultId = defaultLine.name + ";" + defaultName;
	private static final VisumPtLineRouteDirection defaultDirection = VisumPtLineRouteDirection.H;

	private String id;
	private VisumPtLine line;
	private String name;
	private final VisumPtLineRouteDirection direction;

	public VisumPtLineRouteBuilder() {
		super();
		id = defaultId;
		line = defaultLine;
		name = defaultName;
		direction = defaultDirection;
	}

	public VisumPtLineRoute build() {
		return new VisumPtLineRoute(id, line, name, direction);
	}

	public VisumPtLineRouteBuilder uses(VisumTransportSystem transportSystem) {
		line = visumLine().uses(transportSystem).build();
		return this;
	}

	public VisumPtLineRouteBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public VisumPtLineRouteBuilder belongsTo(VisumPtLine line) {
		this.line = line;
		return this;
	}

}
