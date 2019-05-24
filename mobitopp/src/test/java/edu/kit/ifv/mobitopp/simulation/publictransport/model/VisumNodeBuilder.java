package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.visum.VisumNode;

public class VisumNodeBuilder {

	private static final int defaultId = 0;
	private static final String defaultName = "default node";
	private static final int defaultType = 0;
	private static final int defaultX = 0;
	private static final int defaultY = 0;
	private static final int defaultZ = 0;

	private int id;
	private String name;
	private final int type;
	private int x;
	private int y;
	private final int z;

	public VisumNodeBuilder() {
		super();
		id = defaultId;
		name = defaultName;
		type = defaultType;
		x = defaultX;
		y = defaultY;
		z = defaultZ;
	}

	public VisumNode build() {
		return new VisumNode(id, name, type, x, y, z);
	}

	public VisumNodeBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumNodeBuilder at(Point2D coordinate) {
		x = (int) coordinate.getX();
		y = (int) coordinate.getY();
		return this;
	}

	public VisumNodeBuilder at(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
		return this;
	}

  public VisumNodeBuilder withName(String name) {
    this.name = name;
    return this;
  }

}
