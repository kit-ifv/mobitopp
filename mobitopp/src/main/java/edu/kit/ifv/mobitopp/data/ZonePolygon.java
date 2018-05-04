package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.visum.VisumSurface;

import java.io.Serializable;
import java.awt.geom.Point2D;

public class ZonePolygon implements Serializable {

	private static final int dummyAccessEdge = -1;

	private static final long serialVersionUID = 2558509861126978837L;

	private final Location centroid;
	private final VisumSurface polygon;

	public ZonePolygon(VisumSurface polygon, Point2D centerPoint) {
		this(polygon, new Location(new Point2D.Double(centerPoint.getX(), centerPoint.getY()), dummyAccessEdge, 0.0d));
	}

	public ZonePolygon(VisumSurface surface, Location location) {
		super();
		this.polygon = surface;
		this.centroid = location;
	}

	public boolean isPointInside(Point2D point) {
		return this.polygon.isPointInside(point);
	}

	/**
	 * Use cached area to increase performance.
	 * 
	 * @param point
	 * @return
	 */
	public boolean contains(Point2D point) {
		return this.polygon.area().contains(point);
	}

	public Location centroidLocation() {
		return centroid;
	}

	public Point2D centroid() {
		return centroid.coordinate;
	}

	public VisumSurface polygon() {
		return polygon;
	}

}
