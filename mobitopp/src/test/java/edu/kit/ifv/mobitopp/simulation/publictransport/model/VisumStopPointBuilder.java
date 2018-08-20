package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper.dummySet;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStopArea;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

public class VisumStopPointBuilder {

	private static final int defaultId = 0;
	private static final VisumPtStopArea defaultArea = visumStopArea().build();
	private static final String defaultCode = "default code";
	private static final String defaultName = "default name";
	private static final int defaultType = 0;
	private static final VisumTransportSystemSet defaultTransportSystemSet = dummySet();
	private static final boolean defaultDirected = false;
	private static final Point2D defaultCoordinate = new Point2D.Float();

	private int id;
	private VisumPtStopArea stopArea;
	private final String code;
	private String name;
	private final int type;
	private final VisumTransportSystemSet transportSystemSet;
	private final boolean directed;
	private Point2D coordinate;

	public VisumStopPointBuilder() {
		super();
		id = defaultId;
		stopArea = defaultArea;
		code = defaultCode;
		name = defaultName;
		type = defaultType;
		transportSystemSet = defaultTransportSystemSet;
		directed = defaultDirected;
		coordinate = defaultCoordinate;
	}

	@SuppressWarnings("serial")
	public VisumPtStopPoint build() {
		return new VisumPtStopPoint(id, stopArea, code, name, type, transportSystemSet, directed) {

			@Override
			public Point2D coordinate() {
				return coordinate;
			}

		};
	}

	public VisumStopPointBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumStopPointBuilder with(VisumPtStopArea area) {
		stopArea = area;
		return this;
	}

	public VisumStopPointBuilder at(Point2D coordinate) {
		this.coordinate = coordinate;
		return this;
	}

	public VisumStopPointBuilder named(String name) {
		this.name = name;
		return this;
	}

}
