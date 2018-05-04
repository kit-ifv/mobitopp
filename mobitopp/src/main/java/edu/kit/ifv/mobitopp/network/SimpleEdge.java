package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;

public class SimpleEdge 
	implements Edge, Serializable
{

	private static final long serialVersionUID = 1L;

	protected final int id;
	protected SimpleEdge twin;

	protected final Node from;
	protected final Node to;

	protected final Line2D line;


	protected final boolean carAllowed;
	protected final float allowedVelocityInKm;
	protected final float length;
	protected final String transportSystems;

	public SimpleEdge(
		Integer id, 
		Node from,
		Node to,
		VisumOrientedLink vLink, 
		boolean carAllowed
	) {

		this.id=id;
		this.from=from;
		this.to=to;

		this.line = new Line2D.Double(this.from.coordinate(),this.to.coordinate());

		this.carAllowed = carAllowed;
		this.allowedVelocityInKm = allowedVelocityInKm(vLink);
		this.length = length(vLink);
		this.transportSystems = transportSystems(vLink);
	}

	public Edge twin() {
		assert this.twin != null;

		return this.twin;
	}

	public Node from() {
		assert this.from != null;

		return this.from;
	}

	public Node to() {
		assert this.to != null;

		return this.to;
	}

	public int id() {
		return this.id;
	}

	public Line2D line() {

		return this.line;
	}

	public boolean isDegenerated() {

		return line.getP1().distance(line.getP2()) == 0.0;
	}

	public String toString() {
		return "(" + from + ", " + to + ")";
	}

	private float allowedVelocityInKm(VisumOrientedLink vLink) {
		if (vLink == null) { return 0.0f; }

		return vLink.attributes.freeFlowSpeedCar;
	}

	private float length(VisumOrientedLink vLink) {
		if (vLink == null) { return 0.0f; }

		return vLink.length;
	}

	private String transportSystems(VisumOrientedLink vLink) {
		if (vLink == null) { return "<none>"; }

		return vLink.transportSystems.toString();
	}

	public boolean carAllowed() {
		return this.carAllowed;
	}

	public float allowedVelocityInKm() {
		return this.allowedVelocityInKm;
	}

	public float length() {
		return this.length;
	}

	public String transportSystems() {
		return this.transportSystems;
	}

	public double nearestPositionOnEdge(Point2D point) {

		double u1 = line.getX2() - line.getX1();
		double u2 = line.getY2() - line.getY1();

		double l = Math.sqrt(u1*u1 + u2*u2);

		u1 = u1/l;
		u2 = u2/l;

		double p1 = point.getX() - line.getX1();
		double p2 = point.getY() - line.getY1();

		double alpha = p1*u1 + p2*u2;

		
		return Math.max(Math.min(alpha/l,1.0),0.0);

	}

}
