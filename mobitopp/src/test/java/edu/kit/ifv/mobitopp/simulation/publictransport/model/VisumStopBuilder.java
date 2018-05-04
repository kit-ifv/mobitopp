package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.visum.VisumPtStop;

public class VisumStopBuilder {

	private static final int defaultId = 0;
	private static final String defaultCode = "default stop code";
	private static final String defaultName = "default stop";
	private static final int defaultType = 0;
	private static final int defaultX = 0;
	private static final int defaultY = 0;

	private int id;
	private final String code;
	private final String name;
	private final int type;
	private int x;
	private int y;

	public VisumStopBuilder() {
		super();
		id = defaultId;
		code = defaultCode;
		name = defaultName;
		type = defaultType;
		x = defaultX;
		y = defaultY;
	}

	public VisumPtStop build() {
		return new VisumPtStop(id, code, name, type, x, y);
	}

	public VisumStopBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumStopBuilder at(Point2D coordinate) {
		x = (int) coordinate.getX();
		y = (int) coordinate.getY();
		return this;
	}

}
