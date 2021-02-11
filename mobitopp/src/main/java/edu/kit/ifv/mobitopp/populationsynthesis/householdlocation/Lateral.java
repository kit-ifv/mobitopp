package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lateral {

	private final double maximumDistance;

	public Lateral(double maximumDistance) {
		super();
		verify(maximumDistance);
		this.maximumDistance = maximumDistance;
	}

	private void verify(double maximumDistance) {
		if (0.0d > maximumDistance) {
			throw warn(new IllegalArgumentException(
					"Maximum distance must be positive, but was " + maximumDistance), log);
		}
	}

	public Point2D pointAt(Edge edge, double relativePosition, double relativeDistance) {
		verify(edge, relativePosition, relativeDistance);

		double longitudinal_x = edge.to().coordinate().getX() - edge.from().coordinate().getX();
		double longitudinal_y = edge.to().coordinate().getY() - edge.from().coordinate().getY();

		double length = lengthOf(longitudinal_x, longitudinal_y);

		double lateral_x = 0.0 == length ? 1.0 : -longitudinal_y;
		double lateral_y = 0.0 == length ? 1.0 : longitudinal_x;

		double scaling = scalingBy(length);

		assert !Double.isInfinite(scaling) : (longitudinal_x + ", " + longitudinal_y + ","
				+ ((SimpleEdge) edge).length());

		double offsetX = edge.from().coordinate().getX();
		double offsetY = edge.from().coordinate().getY();
		double onEdgeX = relativePosition * longitudinal_x;
		double onEdgeY = relativePosition * longitudinal_y;
		double besideEdgeX = relativeDistance * maximumDistance * scaling * lateral_x;
		double besideEdgeY = relativeDistance * maximumDistance * scaling * lateral_y;
		double x = offsetX + onEdgeX + besideEdgeX;
		double y = offsetY + onEdgeY + besideEdgeY;
		return new Point2D.Double(x, y);
	}

	private double lengthOf(double longitudinal_x, double longitudinal_y) {
		return Math.sqrt(longitudinal_x * longitudinal_x + longitudinal_y * longitudinal_y);
	}

	private double scalingBy(double vlength) {
		double denominator = 0.0 == vlength ? maximumDistance : vlength;
		return 1.0 / denominator;
	}

	private void verify(Edge edge, double relPos, double relDist) {
		if (0.0 > ((SimpleEdge) edge).length()) {
			throw warn(new IllegalArgumentException("Length is negative: " + ((SimpleEdge) edge).length()), log);
		}

		if (0.0d > relPos || 1.0d < relPos) {
			throw warn(new IllegalArgumentException("Relative position is outside the edge: " + relPos), log);
		}

		if (0.0d > relDist || 1.0d < relDist) {
			throw warn(new IllegalArgumentException(
					"Relative distance must be between 0.0 and 1.0, but was " + relDist), log);
		}
	}
}
