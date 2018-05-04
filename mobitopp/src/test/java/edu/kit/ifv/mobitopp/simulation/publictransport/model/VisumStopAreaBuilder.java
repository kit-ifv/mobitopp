package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStop;

import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;

public class VisumStopAreaBuilder {

	private static final int defaultId = 0;
	private static final VisumPtStop defaultStop = visumStop().build();
	private static final String defaultCode = "default stop area code";
	private static final String defaultName = "default stop area";
	private static final VisumNode defaultNode = visumNode().build();
	private static final int defaultType = 0;
	private static final int defaultX = 0;
	private static final int defaultY = 0;

	private int id;
	private VisumPtStop stop;
	private final String code;
	private final String name;
	private VisumNode node;
	private final int type;
	private final int xCoordinate;
	private final int yCoordinate;


	public VisumStopAreaBuilder() {
		super();
		id = defaultId;
		stop = defaultStop;
		code = defaultCode;
		name = defaultName;
		node = defaultNode;
		type = defaultType;
		xCoordinate = defaultX;
		yCoordinate = defaultY;
	}

	public VisumPtStopArea build() {
		return new VisumPtStopArea(id, stop, code, name, node, type, xCoordinate, yCoordinate);
	}

	public VisumStopAreaBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumStopAreaBuilder with(VisumPtStop stop) {
		this.stop = stop;
		return this;
	}

	public VisumStopAreaBuilder with(VisumNode node) {
		this.node = node;
		return this;
	}

}
