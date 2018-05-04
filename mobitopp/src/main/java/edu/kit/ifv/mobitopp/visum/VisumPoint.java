package edu.kit.ifv.mobitopp.visum;

import java.awt.geom.Point2D;

import java.io.Serializable;

public class VisumPoint 
	extends Point2D.Double
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public VisumPoint(double x, double y) {
		super(x, y);
	}

	public VisumPoint(Point2D p) {
		this(p.getX(), p.getY());
	}

	public String toString() {

		return "(" + getX() + "," + getY() + ")";
	}
}
