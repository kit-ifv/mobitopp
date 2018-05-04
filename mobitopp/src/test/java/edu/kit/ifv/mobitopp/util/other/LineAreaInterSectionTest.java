package edu.kit.ifv.mobitopp.util.other;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

public class LineAreaInterSectionTest {

	private final Rectangle2D rect1 = new Rectangle2D.Double(0.0, 0.0, 2.0, 2.0);
	private final Rectangle2D rect2 = new Rectangle2D.Double(1.0, 1.0, 2.0, 2.0);
	private final Rectangle2D rect3 = new Rectangle2D.Double(3.0, 3.0, 2.0, 2.0);

	private final Area area1 = new Area(rect1);
	private final Area area2 = new Area(rect2);
	private final Area area3 = new Area(rect3);

	private final Line2D line1 = new Line2D.Double(-1.0, -1.0, 3.0, 3.0);
	private final Line2D line2 = new Line2D.Double(-1.0, -1.0, 0.5, 0.5);

	@Test
	public void testRectangleLineIntersection() {
		assertTrue(rect1.intersectsLine(line1));
		assertTrue(rect1.intersectsLine(line2));
		assertFalse(rect2.intersectsLine(line2));
	}

	@Test
	public void testAreaAreaIntersection() {
		Area a = new Area(area1);
		a.intersect(area1);

		assertFalse(a.isEmpty());

		a = new Area(area1);
		a.intersect(area2);

		assertFalse(a.isEmpty());

		a = new Area(area1);
		a.intersect(area3);

		assertTrue(a.isEmpty());
	}

	@Test
	public void testLineAsArea() {
		Area la = new Area(line1);
		assertTrue(la.getBounds2D().isEmpty());
	}

}
