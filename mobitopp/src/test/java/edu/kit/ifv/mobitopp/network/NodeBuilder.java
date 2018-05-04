package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Point2D;

public class NodeBuilder {

	private static final int defaultId = 0;
	private static final int defaultX = 0;
	private static final int defaultY = 0;

	private int id;
	private Point2D coordinate;

	public NodeBuilder() {
		super();
		id = defaultId;
		coordinate = new Point2D.Double(defaultX, defaultY);
	}

	public static NodeBuilder node() {
		return new NodeBuilder();
	}

	public Node build() {
		return new SimpleNode(id, coordinate);
	}

	public NodeBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public NodeBuilder at(Point2D coordinate) {
		this.coordinate = coordinate;
		return this;
	}
}
