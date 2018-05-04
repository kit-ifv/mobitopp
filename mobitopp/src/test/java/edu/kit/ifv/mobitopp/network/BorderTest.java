package edu.kit.ifv.mobitopp.network;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

public class BorderTest {

	private Point2D some;
	private Point2D other;
	private Point2D another;
	private Point2D yetAnother;

	@Before
	public void initialise() {
		some = new Point2D.Double(0.0d, 0.0d);
		other = new Point2D.Double(0.0d, 0.0d);
		another = new Point2D.Double(0.0d, 0.0d);
		yetAnother = new Point2D.Double(0.0d, 0.0d);
	}
	
	@Test
	public void intersectsLine() {
		Line2D borderLine = new Line2D.Double(some, other);
		Line2D testLine = new Line2D.Double(another, yetAnother);
		Border border = createBorder(borderLine);
		
		boolean intersects = border.intersects(testLine);
		
		assertTrue(intersects);
	}

	private Border createBorder(Line2D borderLine) {
		return new Border(asList(asList(borderLine)));
	}
}
