package edu.kit.ifv.mobitopp.util.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.junit.Test;

public class AreaUtilTest {

	private final static double EPSILON = 1.0e-16;

	private final Rectangle2D rect1 = new Rectangle2D.Double(0.0, 0.0, 2.0, 2.0);
	private final Rectangle2D rect2 = new Rectangle2D.Double(1.0, 1.0, 2.0, 2.0);
	private final Rectangle2D rect3 = new Rectangle2D.Double(3.0, 3.0, 2.0, 2.0);

	private final Area area1 = new Area(rect1);
	private final Area area2 = new Area(rect2);
	private final Area area3 = new Area(rect3);

	private final Line2D line1 = new Line2D.Double(-1.0, -1.0, 2.0, 2.0);
	private final Line2D line2 = new Line2D.Double(-1.0, -1.0, 0.5, 0.5);

	@Test
	public void testNumberofLines() {
		List<List<Line2D>> lines = AreaUtil.asLines(area1);

		assertEquals(1, lines.size());
		assertEquals(4, lines.get(0).size());
	}

	@Test
	public void testClosed() {
		List<List<Line2D>> lines = AreaUtil.asLines(area1);

		Line2D line = lines.get(0).get(0);

		Point2D p1 = lines.get(0).get(0).getP1();

		assertEquals(0.0, p1.getX(), EPSILON);
		assertEquals(0.0, p1.getY(), EPSILON);

		assertEquals(0.0, p1.getX(), EPSILON);
		assertEquals(0.0, p1.getY(), EPSILON);

		assertEquals(0.0, line.getX1(), EPSILON);
		assertEquals(0.0, line.getY1(), EPSILON);
		assertEquals(0.0, line.getX2(), EPSILON);
		assertEquals(2.0, line.getY2(), EPSILON);
	}

	@Test
	public void testLineIntersectsArea() {
		assertTrue(AreaUtil.lineIntersectsArea(line1, area1));
		assertTrue(AreaUtil.lineIntersectsArea(line1, area2));
		assertFalse(AreaUtil.lineIntersectsArea(line1, area3));

		assertTrue(AreaUtil.lineIntersectsArea(line2, area1));
		assertFalse(AreaUtil.lineIntersectsArea(line2, area2));
		assertFalse(AreaUtil.lineIntersectsArea(line2, area3));
	}

}
