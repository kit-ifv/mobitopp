package edu.kit.ifv.mobitopp.simulation;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Locale;

public class Location implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Point2D coordinate;
	public final int roadAccessEdgeId;
	public final double roadPosition;

	public Location(Point2D coordinate, int roadAccessEdgeId, double roadPosition) {
		verifyRoadPosition(roadPosition);
		this.coordinate = coordinate;
		this.roadAccessEdgeId = roadAccessEdgeId;
		this.roadPosition = roadPosition;
	}

	private void verifyRoadPosition(double roadPosition) {
		if (0.0d > roadPosition || 1.0d < roadPosition) {
			throw new IllegalArgumentException(
					"Road position out of interval [0.0;1.0]: " + roadPosition);
		}
	}

	public int roadAccessEdgeId() {
		return this.roadAccessEdgeId;
	}

	public double roadPosition() {
		return this.roadPosition;
	}

	public Point2D coordinatesP() {
		return this.coordinate;
	}

	public String coordinates() {
		return "(" + coordinate.getX() + "," + coordinate.getY() + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Location other = (Location) obj;
		if (coordinate == null) {
			if (other.coordinate != null) {
				return false;
			}
		} else if (!coordinate.equals(other.coordinate)) {
			return false;
		}
		return true;
	}

	public String toString() {
		return new LocationParser().serialiseRounded(this);
	}

	public String forLogging() {
		return "" 	+ String.format(Locale.ENGLISH, "%.3f",coordinate.getX()) + "; " 
								+ String.format(Locale.ENGLISH, "%.3f",coordinate.getY()) + "; "
								+ roadAccessEdgeId + "; " 
								+ String.format(Locale.ENGLISH, "%.3f", roadPosition)
						+ "";
	}

}
