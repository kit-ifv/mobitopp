package edu.kit.ifv.mobitopp.network;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;

import org.junit.Test;

import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleGraph;
import edu.kit.ifv.mobitopp.network.SimpleNode;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;

public class SimpleEdgeTest {

	final static double EPSILON = 1.0e-6;

	private VisumTransportSystems transportSystems = new VisumTransportSystems(emptyMap());
	private SimpleGraph graph = new SimpleGraph(transportSystems);

	private SimpleNode node00 = graph.makeNode(0, new Point2D.Double(0.0,0.0));
	private SimpleNode node02 = graph.makeNode(0, new Point2D.Double(0.0,2.0));
	private SimpleNode node20 = graph.makeNode(0, new Point2D.Double(2.0,0.0));
	private SimpleNode node22 = graph.makeNode(0, new Point2D.Double(2.0,2.0));

	private SimpleEdge edge0002 = graph.makeEdge(0,node00,node02,null);
	private SimpleEdge edge0020 = graph.makeEdge(0,node00,node20,null);
	private SimpleEdge edge0022 = graph.makeEdge(0,node00,node22,null);
	


	@Test
	public void testDummy() {

		assertEquals(2,1+1);
		assertEquals(3,2+1);
	}

	@Test
	public void testNearestPositionOnEdgeBaseInner() {

		Point2D point = new Point2D.Double(1.0,1.0);

		assertEquals(0.5, edge0002.nearestPositionOnEdge(point), EPSILON);
		assertEquals(0.5, edge0020.nearestPositionOnEdge(point), EPSILON);
	}

	@Test
	public void testNearestPositionOnEdgeBaseAbove() {

		Point2D point = new Point2D.Double(4.0,3.0);

		assertEquals(1.0, edge0002.nearestPositionOnEdge(point), EPSILON);
		assertEquals(1.0, edge0020.nearestPositionOnEdge(point), EPSILON);
	}

	@Test
	public void testNearestPositionOnEdgeBaseBelow() {

		Point2D point = new Point2D.Double(-1.0,-2.0);

		assertEquals(0.0, edge0002.nearestPositionOnEdge(point), EPSILON);
		assertEquals(0.0, edge0020.nearestPositionOnEdge(point), EPSILON);
	}

	@Test
	public void testNearestPositionOnEdgeInner() {

		Point2D point1 = new Point2D.Double(2.0,0.0);
		Point2D point2 = new Point2D.Double(0.0,2.0);

		assertEquals(0.5, edge0022.nearestPositionOnEdge(point1), EPSILON);
		assertEquals(0.5, edge0022.nearestPositionOnEdge(point2), EPSILON);
	}


	@Test
	public void testNearestPositionOnEdgeUpperEnd() {

		Point2D point1 = new Point2D.Double(4.0,0.0);
		Point2D point2 = new Point2D.Double(0.0,4.0);

		assertEquals(1.0, edge0022.nearestPositionOnEdge(point1), EPSILON);
		assertEquals(1.0, edge0022.nearestPositionOnEdge(point2), EPSILON);
	}

	@Test
	public void testNearestPositionOnEdgeAbove() {

		Point2D point1 = new Point2D.Double(5.0,0.0);
		Point2D point2 = new Point2D.Double(0.0,5.0);

		assertEquals(1.0, edge0022.nearestPositionOnEdge(point1), EPSILON);
		assertEquals(1.0, edge0022.nearestPositionOnEdge(point2), EPSILON);
	}

	@Test
	public void testNearestPositionOnBelow() {

		Point2D point1 = new Point2D.Double(-1.0,0.0);
		Point2D point2 = new Point2D.Double(0.0,-2.0);

		assertEquals(0.0, edge0022.nearestPositionOnEdge(point1), EPSILON);
		assertEquals(0.0, edge0022.nearestPositionOnEdge(point2), EPSILON);
	}


}

