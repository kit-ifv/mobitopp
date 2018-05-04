package edu.kit.ifv.mobitopp.routing;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import edu.kit.ifv.mobitopp.routing.util.SimplePQ;

@RunWith(Parameterized.class)
public class RouterTest {

	@Parameter
	public Router dijkstra;
	
	@Parameters(name = "{index} - {0}")
	public static Iterable<Router> router() {
		ArrayList<Router> router = new ArrayList<>();
		router.add(new Dijkstra(new SimplePQ<>()));
		router.add(new ForwardDijkstra(new SimplePQ<>()));
		router.add(new DijkstraFibonacci());
		return router;
	}
	
	private ExampleNetwork network;

	@Before 
	public void setUp() {
		network = ExampleNetwork.createDefault();
	}

	@Test 
	public void testDirectNeighbour() {
	
		Path p = dijkstra.shortestPath(graph(), node1(),node2());

		assertEquals("Path should contain only 1 element", 1, p.size());
		assertEquals("testDirectNeighbour", link12(), p.first());
		assertEquals("Path should have same length as link12", 2.0f, p.length(), 0.001f);
	}

	@Test 
	public void testPathOfSize2() {

		Path p = dijkstra.shortestPath(graph(), node1(),node3());

		assertEquals("Path should contain 2 elements", 2, p.size());
		assertEquals("First element should be (eins,zwei)", link12(), p.first());
		assertEquals("Second element should be (zwei,drei)", link23(), p.next(p.first()));
		assertEquals("Path length should be 3", 3.0f, p.length(), 0.001f);

	}


	@Test 
	public void testPathOfSize4() {

		Path p = dijkstra.shortestPath(graph(), node1(),node5());

		assertEquals("Path should contain 4 elements", 4, p.size());
		assertEquals("First element should be (eins,zwei)", link12(), p.first());
		assertEquals("Second element should be (zwei,drei)", link23(), p.next(p.first()));
		assertEquals("Path length should be 5", 5.0f, p.length(), 0.001f);

	}

	@Test
	public void shortestFromAll() {
		Dijkstra dijkstra = new Dijkstra(new SimplePQ<Node>());
		
		Map<Node, Path> paths = dijkstra.shortestPathFromAll(graph(), node5());
		
		assertThat(paths.keySet(), containsInAnyOrder(node1(), node2(), node3(), node4(), node5()));
	}
	
	@Test
	public void shortestToAll() {
		Dijkstra dijkstra = new Dijkstra(new SimplePQ<Node>());
		
		Map<Node, Path> paths = dijkstra.shortestPathToAll(graph(), node1());
		
		assertThat(paths.keySet(), containsInAnyOrder(node1(), node2(), node3(), node4(), node5()));
	}

	private DefaultGraph graph() {
		return network.graph;
	}

	private DefaultNode node1() {
		return network.node1;
	}

	private DefaultNode node2() {
		return network.node2;
	}

	private DefaultNode node3() {
		return network.node3;
	}

	private DefaultNode node4() {
		return network.node4;
	}

	private DefaultNode node5() {
		return network.node5;
	}

	private DefaultLink link12() {
		return network.link12;
	}

	private DefaultLink link23() {
		return network.link23;
	}

}
