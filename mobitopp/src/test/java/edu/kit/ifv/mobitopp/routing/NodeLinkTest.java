package edu.kit.ifv.mobitopp.routing;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class NodeLinkTest {

	private DefaultNode node1;
	private DefaultNode node2;
	private DefaultNode node3;
	private DefaultNode node4;
	private DefaultNode node5;
	private DefaultLink link45;
	private DefaultLink link12;
	private DefaultLink link13;
	private DefaultLink link14;
	private DefaultLink link15;
	private DefaultLink link23;
	private DefaultLink link34;

	private static boolean contains(Edge[] hay, Edge needle) {
		for (int i = 0; i < hay.length; i++) {
			if (needle == hay[i]) {
				return true;
			}
		}
		return false;
	}

	@Before
	public void setUp() {
		node1 = new DefaultNode("eins");
		node2 = new DefaultNode("zwei");
		node3 = new DefaultNode("drei");
		node4 = new DefaultNode("vier");
		node5 = new DefaultNode("fünf");

		link12 = new DefaultLink("L12", node1, node2, 2.0f, 2.0f);
		link13 = new DefaultLink("L13", node1, node3, 6.0f, 6.0f);
		link14 = new DefaultLink("L14", node1, node4, 8.0f, 8.0f);
		link15 = new DefaultLink("L15", node1, node5, 10.0f, 10.0f);
		link23 = new DefaultLink("L23", node2, node3, 1.0f, 1.0f);
		link34 = new DefaultLink("L34", node3, node4, 1.0f, 1.0f);
		link45 = new DefaultLink("L45", node4, node5, 1.0f, 1.0f);

		node1.setOutgoingLinks(new ArrayList<>(asList(link12, link13, link14, link15)));
		node2.setOutgoingLinks(new ArrayList<>(asList(link23)));
		node3.setOutgoingLinks(new ArrayList<>(asList(link34)));
		node4.setOutgoingLinks(new ArrayList<>(asList(link45)));

		node2.setIncomingLinks(new ArrayList<>(asList(link12)));
		node3.setIncomingLinks(new ArrayList<>(asList(link13, link23)));
		node4.setIncomingLinks(new ArrayList<>(asList(link14, link34)));
		node5.setIncomingLinks(new ArrayList<>(asList(link15, link45)));
	}

	@Test
	public void testLinkFrom() {
		assertEquals("Link.from(...)", node1, link12.from());
		assertEquals("Link.from(...)", node1, link13.from());
		assertEquals("Link.from(...)", node1, link14.from());
		assertEquals("Link.from(...)", node1, link15.from());
		assertEquals("Link.from(...)", node2, link23.from());
		assertEquals("Link.from(...)", node3, link34.from());
		assertEquals("Link.from(...)", node4, link45.from());
	}

	@Test
	public void testLinkTo() {
		assertEquals("Link.to(...)", node2, link12.to());
		assertEquals("Link.to(...)", node3, link13.to());
		assertEquals("Link.to(...)", node4, link14.to());
		assertEquals("Link.to(...)", node5, link15.to());
		assertEquals("Link.to(...)", node3, link23.to());
		assertEquals("Link.to(...)", node4, link34.to());
		assertEquals("Link.to(...)", node5, link45.to());
	}

	@Test
	public void testNodeInLinksCount() {
		assertEquals("Node.inLinks(...)", 0, node1.incomingEdges().length);
		assertEquals("Node.inLinks(...)", 1, node2.incomingEdges().length);
		assertEquals("Node.inLinks(...)", 2, node3.incomingEdges().length);
		assertEquals("Node.inLinks(...)", 2, node4.incomingEdges().length);
		assertEquals("Node.inLinks(...)", 2, node5.incomingEdges().length);
	}

	@Test
	public void testNodeOutLinksCount() {
		assertEquals("Node.outLinks(...)", 4, node1.outgoingEdges().length);
		assertEquals("Node.outLinks(...)", 1, node2.outgoingEdges().length);
		assertEquals("Node.outLinks(...)", 1, node3.outgoingEdges().length);
		assertEquals("Node.outLinks(...)", 1, node4.outgoingEdges().length);
		assertEquals("Node.outLinks(...)", 0, node5.outgoingEdges().length);
	}

	@Test
	public void testNodeInLinks() {
		assertTrue("Node.inLinks(...)", contains(node2.incomingEdges(), link12));
		assertTrue("Node.inLinks(...)", contains(node3.incomingEdges(), link13));
		assertTrue("Node.inLinks(...)", contains(node3.incomingEdges(), link23));
		assertTrue("Node.inLinks(...)", contains(node4.incomingEdges(), link14));
		assertTrue("Node.inLinks(...)", contains(node4.incomingEdges(), link34));
		assertTrue("Node.inLinks(...)", contains(node5.incomingEdges(), link15));
		assertTrue("Node.inLinks(...)", contains(node5.incomingEdges(), link45));
	}

	@Test
	public void testNodeOutLinks() {
		assertTrue("Node.outLinks(...)", contains(node1.outgoingEdges(), link12));
		assertTrue("Node.outLinks(...)", contains(node1.outgoingEdges(), link13));
		assertTrue("Node.outLinks(...)", contains(node1.outgoingEdges(), link14));
		assertTrue("Node.outLinks(...)", contains(node1.outgoingEdges(), link15));
		assertTrue("Node.outLinks(...)", contains(node2.outgoingEdges(), link23));
		assertTrue("Node.outLinks(...)", contains(node3.outgoingEdges(), link34));
		assertTrue("Node.outLinks(...)", contains(node4.outgoingEdges(), link45));
	}

}
