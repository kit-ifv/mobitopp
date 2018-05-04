package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Point2D;
import java.io.Serializable;

import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;


public class SimpleNode 
	implements Node, Serializable
{

	private static final long serialVersionUID = 1L;

	private final int id;
	private final Point2D coord;

	public SimpleNode(int id, Point2D point) {
		super();
		this.id = id;
		this.coord = new Point2D.Double(point.getX(), point.getY());
	}

	protected SimpleNode(VisumNode vNode) {
		this(vNode.id(), vNode.coordinate());
	}

	protected SimpleNode(int id, VisumPoint2 vPoint) {
		this(id, new Point2D.Float(vPoint.x, vPoint.y));
	}

	public int id() {
		return this.id;
	}

	public Point2D coordinate() {
		return (Point2D) this.coord.clone();
	}

	public String toString() {
		return "(" + coord.getX() + "," + coord.getY() +  ")";
	}

}
