package edu.kit.ifv.mobitopp.util.geom;

import java.awt.geom.PathIterator;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import java.util.List;
import java.util.ArrayList;


public class AreaUtil {

	public static boolean lineIntersectsArea(Line2D line, Area area) {
		List<List<Line2D>> areaLines = asLines(area);
		return lineIntersectsLines(line, areaLines);
	}

	public static boolean lineIntersectsLines(Line2D line, List<List<Line2D>> areaLines) {
		for (List<Line2D> polygon : areaLines) {
			for (Line2D edge : polygon) {

				if (line.intersectsLine(edge)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<List<Line2D>> asLines(Area area) {

		List<List<Line2D>> result = new ArrayList<List<Line2D>>();

		double[] coords = new double[6];

		List<Point2D> points = new ArrayList<Point2D>();

		for (PathIterator iter = area.getPathIterator(null); !iter.isDone(); iter.next()) {

			int status = iter.currentSegment(coords);

			assert status == java.awt.geom.PathIterator.SEG_MOVETO
					|| status == java.awt.geom.PathIterator.SEG_LINETO
					|| status == java.awt.geom.PathIterator.SEG_CLOSE;

			if (status == java.awt.geom.PathIterator.SEG_MOVETO) {

				if (!points.isEmpty()) {
					result.add(pointsAsLines(points));
					points = new ArrayList<Point2D>();
				}

				Point2D point = new Point2D.Double(coords[0], coords[1]);
		
				points = new ArrayList<Point2D>();

				points.add(point);
			} else if (status == java.awt.geom.PathIterator.SEG_LINETO) {
				Point2D point = new Point2D.Double(coords[0], coords[1]);

				points.add(point);
			}  else if (status == java.awt.geom.PathIterator.SEG_CLOSE) {

				points.add(points.get(0));

				result.add(pointsAsLines(points));
				points = new ArrayList<Point2D>();
			} else {
				throw new AssertionError();
			}
		}

		if (!points.isEmpty()) {
			result.add(pointsAsLines(points));
			points = new ArrayList<Point2D>();
		}

		return result;
	}

	public static List<Line2D> pointsAsLines(List<Point2D> points) {

		List<Line2D> lines = new ArrayList<Line2D>();

		for (int i=0; i<points.size()-1; i++) {
			Line2D line = new Line2D.Double(points.get(i), points.get(i+1));
			lines.add(line);
		}

		return lines;
	}

}

