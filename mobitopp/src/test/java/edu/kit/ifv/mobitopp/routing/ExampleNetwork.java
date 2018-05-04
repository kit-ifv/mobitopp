package edu.kit.ifv.mobitopp.routing;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;

public class ExampleNetwork {

	public final DefaultGraph graph;
	public final DefaultNode node1;
	public final DefaultNode node2;
	public final DefaultNode node3;
	public final DefaultNode node4;
	public final DefaultNode node5;
	public final DefaultLink link12;
	public final DefaultLink link23;

	public ExampleNetwork(
			DefaultGraph graph, DefaultNode node1, DefaultNode node2, DefaultNode node3,
			DefaultNode node4, DefaultNode node5, DefaultLink link12, DefaultLink link23) {
		super();
		this.graph = graph;
		this.node1 = node1;
		this.node2 = node2;
		this.node3 = node3;
		this.node4 = node4;
		this.node5 = node5;
		this.link12 = link12;
		this.link23 = link23;
	}

	public static ExampleNetwork createDefault() {
		DefaultNode node1 = new DefaultNode("eins");
		DefaultNode node2 = new DefaultNode("zwei");
		DefaultNode node3 = new DefaultNode("drei");
		DefaultNode node4 = new DefaultNode("vier");
		DefaultNode node5 = new DefaultNode("fünf");

		DefaultLink link12 = new DefaultLink("L12", node1, node2, 2.0f, 2.0f);
		DefaultLink link13 = new DefaultLink("L13", node1, node3, 6.0f, 6.0f);
		DefaultLink link14 = new DefaultLink("L14", node1, node4, 8.0f, 8.0f);
		DefaultLink link15 = new DefaultLink("L15", node1, node5, 10.0f, 10.0f);
		DefaultLink link23 = new DefaultLink("L23", node2, node3, 1.0f, 1.0f);
		DefaultLink link34 = new DefaultLink("L34", node3, node4, 1.0f, 1.0f);
		DefaultLink link45 = new DefaultLink("L45", node4, node5, 1.0f, 1.0f);
		
		node1.setOutgoingLinks(new ArrayList<>(asList(link12, link13, link14, link15)));
		node2.setOutgoingLinks(new ArrayList<>(asList(link23)));
		node3.setOutgoingLinks(new ArrayList<>(asList(link34)));
		node4.setOutgoingLinks(new ArrayList<>(asList(link45)));

		node2.setIncomingLinks(new ArrayList<>(asList(link12)));
		node3.setIncomingLinks(new ArrayList<>(asList(link13, link23)));
		node4.setIncomingLinks(new ArrayList<>(asList(link14, link34)));
		node5.setIncomingLinks(new ArrayList<>(asList(link15, link45)));

		DefaultGraph graph = new DefaultGraph(
				new ArrayList<DefaultNode>(Arrays.asList(node1, node2, node3, node4, node5)),
				new ArrayList<DefaultLink>(
						Arrays.asList(link12, link13, link14, link15, link23, link34, link45)));

		return new ExampleNetwork(graph, node1, node2, node3, node4, node5, link12, link23);
	}
}
