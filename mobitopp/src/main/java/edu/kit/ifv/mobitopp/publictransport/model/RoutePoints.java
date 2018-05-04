package edu.kit.ifv.mobitopp.publictransport.model;

import static java.util.Collections.emptyList;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RoutePoints {

	private final List<Point2D> points;
	private final Stop start;
	private final Stop end;

	private RoutePoints(Stop start, Stop end, List<Point2D> points) {
		super();
		this.start = start;
		this.end = end;
		this.points = Collections.unmodifiableList(points);
	}

	public static RoutePoints from(Stop stop, Stop neighbour) {
		return from(stop, neighbour, emptyList());
	}
	
	public static RoutePoints from(Stop start, Stop end, List<Point2D> via) {
		List<Point2D> startIncluded = startsWith(start, via);
		List<Point2D> bothIncluded = endsWith(end, startIncluded);
		return new RoutePoints(start, end, bothIncluded);
	}

	private static List<Point2D> startsWith(Stop start, List<Point2D> points) {
		Point2D startLocation = start.coordinate();
		if (points.isEmpty()) {
			return Collections.singletonList(startLocation);
		}
		ArrayList<Point2D> containsStart = new ArrayList<>();
		if (startsNotAt(startLocation, points)) {
			containsStart.add(startLocation);
		}
		containsStart.addAll(points);
		return containsStart;
	}

	private static boolean startsNotAt(Point2D startLocation, List<Point2D> points) {
		return !startLocation.equals(firstLocation(points));
	}

	private static Point2D firstLocation(List<Point2D> points) {
		return points.get(0);
	}

	private static List<Point2D> endsWith(Stop end, List<Point2D> points) {
		Point2D endLocation = end.coordinate();
		if (points.isEmpty()) {
			return Collections.singletonList(endLocation);
		}
		ArrayList<Point2D> containsEnd = new ArrayList<>(points);
		if (endsNotAt(endLocation, points)) {
			containsEnd.add(endLocation);
		}
		return containsEnd;
	}

	private static boolean endsNotAt(Point2D endLocation, List<Point2D> points) {
		return !endLocation.equals(lastLocation(points));
	}

	private static Point2D lastLocation(List<Point2D> points) {
		return points.get(points.size() - 1);
	}

	/**
	 * Returns an unmodifiable list of {@link Point2D}.
	 * 
	 * @return an unmodifiable list of {@link Point2D}
	 */
	public List<Point2D> toList() {
		return Collections.unmodifiableList(points);
	}

	public void forEach(Consumer<Point2D> consumer) {
		points.forEach(consumer);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
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
		RoutePoints other = (RoutePoints) obj;
		if (points == null) {
			if (other.points != null) {
				return false;
			}
		} else if (!points.equals(other.points)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RoutePoints [start=" + start + ", end=" + end + ", points=" + points + "]";
	}

}
