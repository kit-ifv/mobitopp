package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;

import java.util.Collections;

import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class VisumConnectorBuilder {

	private static final VisumZone defaultZone = visumZone().build();
	private static final VisumNode defaultNode = visumNode().build();
	private static final String sourceDirection = "Q";
	private static final String destinationDirection = "Z";
	private static final int defaultType = 1;
	private static final String defaultCode = "P";
	private static final float defaultLength = 1.0f;
	private static final int defaultTravelTimeInSeconds = 1;
	
	private VisumZone zone;
	private VisumNode node;
	private String direction;
	private int type;
	private VisumTransportSystemSet transportSystems;
	private float length;
	private int travelTimeInSeconds;
	
	public VisumConnectorBuilder() {
		super();
		zone = defaultZone;
		node = defaultNode;
		direction = sourceDirection;
		type = defaultType;
		transportSystems = defaultTransportSystemSet();
		length = defaultLength;
		travelTimeInSeconds = defaultTravelTimeInSeconds;
	}
	
	private static VisumTransportSystemSet defaultTransportSystemSet() {
		return VisumTransportSystemSet.getByCode(defaultCode, systems());
	}

	private static VisumTransportSystems systems() {
		return new VisumTransportSystems(Collections.emptyMap());
	}

	public VisumConnector build() {
		return new VisumConnector(zone, node, direction, type, transportSystems, length, travelTimeInSeconds);
	}

	public VisumConnectorBuilder with(VisumZone zone) {
		this.zone = zone;
		return this;
	}

	public VisumConnectorBuilder with(VisumNode node) {
		this.node = node;
		return this;
	}
	
	public VisumConnectorBuilder origin() {
		direction = sourceDirection;
		return this;
	}
	
	public VisumConnectorBuilder destination() {
		direction = destinationDirection;
		return this;
	}

}
