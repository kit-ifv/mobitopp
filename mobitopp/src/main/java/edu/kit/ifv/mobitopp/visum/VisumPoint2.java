package edu.kit.ifv.mobitopp.visum;

import java.awt.geom.Point2D;

import java.io.Serializable;

public class VisumPoint2 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final float x;
	public final float y;

	public VisumPoint2(float x, float y) {
		this.x=x;
		this.y=y;
	}

	public String toString() {

		return "(" + x + "," + y + ")";
	}

	public Point2D asPoint2D() {
		return new Point2D.Float(x,y);
	}
}
