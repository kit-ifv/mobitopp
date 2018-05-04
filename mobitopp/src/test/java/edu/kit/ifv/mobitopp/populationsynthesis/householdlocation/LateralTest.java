package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleNode;

public class LateralTest {

	private Node start;
	private Node end;
	private int startId;
	private int endId;
	private SimpleEdge edge;
	private Lateral lateral;
	private double relativePosition;
	private double relativeDistance;

	@Before
	public void initialise() {
		relativePosition = 0.5d;
		relativeDistance = 1.0d;
		double maximumDistance = 1.0d;
		edge = mock(SimpleEdge.class);
		startId = 0;
		endId = 2;
		start = newNode(startId);
		end = newNode(endId);
		when(edge.length()).thenReturn(2.0f);
		lateral = new Lateral(maximumDistance);
	}

	@Test
	public void pointIsLateralToEdge() {
		when(edge.from()).thenReturn(start);
		when(edge.to()).thenReturn(end);
		Point2D point = calculatePoint();

		assertThat(point, is(equalTo(new Point2D.Double(1, relativeDistance))));
	}
	
	@Test
	public void anotherPointIsLateralToEdge() {
		SimpleNode anotherStart = new SimpleNode(1, new Point2D.Double(0, 1));
		SimpleNode anotherEnd = new SimpleNode(3, new Point2D.Double(0, 3));
		when(edge.from()).thenReturn(anotherStart);
		when(edge.to()).thenReturn(anotherEnd);
		Point2D point = calculatePoint();
		
		assertThat(point, is(equalTo(new Point2D.Double(-relativeDistance, 2))));
	}
	
	@Test
	public void startAndEndIsSame() {
		when(edge.from()).thenReturn(start);
		when(edge.to()).thenReturn(start);
		
		Point2D point = calculatePoint();
		
		assertThat(point, is(equalTo(new Point2D.Double(1, relativeDistance))));
	}
	
	@Test
	public void greaterMaximumDistance() {
		double maximumDistance = 2.0d;
		when(edge.from()).thenReturn(start);
		when(edge.to()).thenReturn(end);
		
		Point2D point = new Lateral(maximumDistance).pointAt(edge, relativePosition,
				relativeDistance);
		
		assertThat(point, is(equalTo(new Point2D.Double(1, maximumDistance))));
	}

	private Point2D calculatePoint() {
		return lateral.pointAt(edge, relativePosition, relativeDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void incorrectMaximumDistance() {
		double tooLowMaximumDistance = -1.0d;
		new Lateral(tooLowMaximumDistance);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failWithIncorrectEdge() {
		when(edge.length()).thenReturn(-1.0f);

		calculatePoint();
	}

	@Test(expected = IllegalArgumentException.class)
	public void relativePositionTooLow() {
		double tooLow = -1.0d;
		lateral.pointAt(edge, tooLow, relativeDistance);
	}

	@Test(expected = IllegalArgumentException.class)
	public void relativePositionTooHigh() {
		double tooHigh = 2.0d;
		lateral.pointAt(edge, tooHigh, relativeDistance);
	}

	@Test(expected = IllegalArgumentException.class)
	public void relativeDistanceTooLow() {
		double tooLow = -1.0d;
		lateral.pointAt(edge, relativePosition, tooLow);
	}

	@Test(expected = IllegalArgumentException.class)
	public void relativeDistanceTooHigh() {
		double tooHigh = 2.0d;
		lateral.pointAt(edge, relativePosition, tooHigh);
	}

	private SimpleNode newNode(int id) {
		return new SimpleNode(id, new Point2D.Double(id, 0));
	}
}
