package edu.kit.ifv.mobitopp.routing;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumConnector;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLink;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumOrientedLink;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.routing.util.SimplePQ;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class DijkstraVisumTest {

	private static VisumTransportSystem carSystem;

	private Dijkstra dijkstra;
	private VisumZone zone1;
	private VisumZone zone2;
	private VisumZone zone3;
	private VisumNode node1;
	private VisumNode node2;
	private VisumNode node3;
	private VisumNode node4;
	private VisumNode node5;
	private VisumLink link12;
	private VisumLink link34;
	private VisumConnector connector11;
	private VisumConnector connector22;
	private VisumConnector connector23;
	private VisumConnector connector35;
	private VisumNetwork visum;

	private GraphFromVisumNetwork graph;
	private Map<Node, Path> paths;

	@BeforeClass
	public static void initialiseClass()
			throws NoSuchFieldException, SecurityException, IllegalAccessException {
		ReflectionHelper.clearTransportSystemSetCache();
		carSystem = new VisumTransportSystem("P", "P", "P");
	}

	@Before
	public void initialise() {
		dijkstra = new Dijkstra(new SimplePQ<Node>());
		zone1 = visumZone().withId(1).internal().build();
		zone2 = visumZone().withId(2).external().build();
		zone3 = visumZone().withId(3).internal().build();
		node1 = visumNode().withId(1).build();
		node2 = visumNode().withId(2).build();
		node3 = visumNode().withId(3).build();
		node4 = visumNode().withId(4).build();
		node5 = visumNode().withId(5).build();
		link12 = link(carSystem, node1, node2, 1);
		link34 = link(carSystem, node3, node4, 2);
		connector11 = visumConnector().with(zone1).with(node1).origin().build();
		connector22 = visumConnector().with(zone2).with(node2).destination().build();
		connector23 = visumConnector().with(zone2).with(node3).origin().build();
		connector35 = visumConnector().with(zone3).with(node5).destination().build();
		visum = createVisumNetwork();
	}

	private VisumNetwork createVisumNetwork() {
		VisumTransportSystem car = new VisumTransportSystem("P", "P", "P");
		return visumNetwork()
				.with(car)
				.with(zone1)
				.with(zone2)
				.with(node1)
				.with(node2)
				.with(node3)
				.with(node4)
				.with(link12)
				.with(link34)
				.addConnector(zone1, connector11)
				.addConnector(zone2, connector22)
				.addConnector(zone2, connector23)
				.addConnector(zone3, connector35)
				.build();
	}

	@Test
	public void simpleShortestToAll() {
		graph = new GraphFromVisumNetwork(visum);

		searchPaths();

		verifyUntilFirstConnector();
		assertThat(paths, not(hasKey(node3)));
		assertThat(paths, not(hasKey(node4)));
	}

	@Test
	public void complexShortestToAll() {
		graph = new GraphFromVisumNetwork(visum, NodeFromVisumZone::useExternalInRouteSeach);

		searchPaths();

		verifyUntilFirstConnector();
		assertThat(pathTo(node3), is(equalTo(pathToNode3())));
		assertThat(pathTo(node4), is(equalTo(pathToNode4())));
		assertThat(paths, not(hasKey(zone3())));
		assertThat(paths, not(hasKey(node5)));
	}

	private void searchPaths() {
		Node startZone = nodeOf(zone1);
		paths = dijkstra.shortestPathToAll(graph, startZone);
	}

	private void verifyUntilFirstConnector() {
		assertThat(pathTo(node1), is(equalTo(pathToNode1())));
		assertThat(pathTo(node2), is(equalTo(pathToNode2())));
		assertThat(pathTo(zone2()), is(equalTo(pathToZone2())));
	}
	
	@Test
	public void shortestPathToZones() {
		graph = new GraphFromVisumNetwork(visum);
		
		Node start = nodeOf(zone1);
		paths = dijkstra.shortestPathToAllZones(graph, start);
		
		assertThat(paths.entrySet(), hasSize(2));
		assertThat(pathTo(zone1()), is(DefaultPath.emptyPath));
		assertThat(pathTo(zone2()), is(equalTo(pathToZone2())));
	}
	
	private Node zone1() {
		return nodeOf(zone1);
	}

	private Node zone2() {
		return nodeOf(zone2);
	}

	private Node nodeOf(VisumZone zone) {
		return graph.zones.get(zone.id);
	}

	private Node zone3() {
		return nodeOf(zone3);
	}

	private DefaultPath pathToNode1() {
		int notNeededTravelTime = 0;
		return new DefaultPath(asList(connector11()), notNeededTravelTime);
	}

	private DefaultPath pathToNode2() {
		return new DefaultPath(asList(connector11(), link12()), 0);
	}

	private DefaultPath pathToNode3() {
		return new DefaultPath(asList(connector11(), link12(), connector22(), connector23()), 0);
	}

	private DefaultPath pathToNode4() {
		return new DefaultPath(asList(connector11(), link12(), connector22(), connector23(), link34()),
				0);
	}

	private DefaultPath pathToZone2() {
		return new DefaultPath(asList(connector11(), link12(), connector22()), 0);
	}

	private Path pathTo(VisumNode node) {
		return pathTo(nodeFor(node));
	}

	private Node nodeFor(VisumNode node) {
		return graph.nodeFor(node.id());
	}

	private Path pathTo(Node node) {
		return paths.get(node);
	}

	private Link link12() {
		return graph.linkFor(link12.id);
	}

	private Link link34() {
		return graph.linkFor(link34.id);
	}

	private VisumLink link(
			VisumTransportSystem carSystem, VisumNode node1, VisumNode node2, int linkId) {
		VisumOrientedLink forward12 = visumOrientedLink()
				.withId(linkId + ":1")
				.from(node1)
				.to(node2)
				.withLength(1.0f)
				.withCarCapacity(1)
				.withFreeFlowSpeedCar(1)
				.with(carSystem)
				.build();
		VisumOrientedLink backward12 = visumOrientedLink()
				.withId(linkId + ":2")
				.to(node1)
				.from(node2)
				.withLength(1.0f)
				.withCarCapacity(1)
				.withFreeFlowSpeedCar(1)
				.with(carSystem)
				.build();
		return visumLink().withId(linkId).withForward(forward12).withBackward(backward12).build();
	}

	private Link connector11() {
		return graph.connectors.get(0);
	}

	private Link connector22() {
		return graph.connectors.get(1);
	}

	private Link connector23() {
		return graph.connectors.get(2);
	}
}
