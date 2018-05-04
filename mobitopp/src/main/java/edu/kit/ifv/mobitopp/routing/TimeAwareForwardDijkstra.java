package edu.kit.ifv.mobitopp.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;


public class TimeAwareForwardDijkstra {

	PriorityQueue<Node> forward;
	HashMap<Node,Float> dist_fw;
	HashMap<Node,Edge> prev_fw;

	float startTime = 0.0f;

	int deleteMinOperations = 0;
	private final float latestArrival;
	private final Set<Node> foundTargets;
	private boolean findAtLeastOneTarget = false;

	public TimeAwareForwardDijkstra(PriorityQueue<Node> queue) {
		this(queue, Float.MAX_VALUE);
	}

	public TimeAwareForwardDijkstra(PriorityQueue<Node> queue, float latestArrival) {
		super();
		this.latestArrival = latestArrival;
		forward = queue.createNew();
		foundTargets = new HashSet<>();
	}

	public Path shortestPath(Graph g, TravelTime tt, Node s, Node t, float startTime) {

		assert s != null;
		assert t != null;

		if (s == t) {
			return DefaultPath.emptyPath;
		}
		assert s != t;

		initDataStructures(g, startTime);

		dijkstraOneToOne(g, tt, s, t);
		Path p = unpackPath(g, t, prev_fw);

		return p;
	}

	public Map<Node,Path> shortestPathToAllZones(
		GraphFromVisumNetwork g,
		TravelTime tt,
		Node s,
		float startTime
	) {

		assert s != null;

		initDataStructures(g, startTime);

		dijkstraOneToAll(g, tt, s);

		Map<Node,Path> paths = new HashMap<>();

		for (Node zone : g.zones.values()) {

			if (s == zone) {
				paths.put(zone,DefaultPath.emptyPath);
			} else {

				Path p = unpackPath(g, zone, prev_fw);

				if (p.isValid()) {
					paths.put(zone, p);
				}
			}
		}

		return paths;
	}

	public Map<Node,Path> shortestPathToAll(
		GraphFromVisumNetwork g,
		TravelTime tt,
		Node s,
		Float startTime
	) {

		assert s != null;

		initDataStructures(g, startTime);

		dijkstraOneToAll(g, tt, s);

		Map<Node,Path> paths = new HashMap<>();

		for (Node node : g.allNodes) {

			if (s == node) {
				paths.put(node,DefaultPath.emptyPath);
			} else {
				Path p = unpackPath(g, node, prev_fw);

				if (p.isValid()) {
					paths.put(node, p);
				}
			}
		}

		return paths;
	}

	public Map<Node, Path> shortestPathToTargets(
			GraphFromVisumNetwork graph, TravelTime travelTime, Node start, float startTime,
			int targetsToResolve) {

		assert start != null;

		initDataStructures(graph, startTime);

		dijkstraOneToTargets(graph, travelTime, start, targetsToResolve);

		Map<Node,Path> paths = new HashMap<>();

		for (Node node : graph.allNodes) {
			if (!node.isTarget()){
				continue;
			}
			if (start == node) {
				paths.put(node,DefaultPath.emptyPath);
			} else {
				Path p = unpackPath(graph, node, prev_fw);

				if (p.isValid()) {
					paths.put(node, p);
				}
			}
		}

		return paths;
	}

	protected void initDataStructures(Graph g, float startTime) {

		Node[] allNodes = g.nodes();

		dist_fw = new HashMap<>(allNodes.length, 1.0f);
		prev_fw = new HashMap<>(allNodes.length, 1.0f);

		forward = forward.createNew();

		deleteMinOperations = 0;

		this.startTime = startTime;
	}

	protected void dijkstraOneToAll(Graph g, TravelTime tt, Node s) {

		assert forward.isEmpty();

		dist_fw.put(s, startTime);
		relaxOutgoingEdges(g, tt, s);

		assert dist_fw != null;
		assert prev_fw != null;
		assert forward != null;

		boolean finished = false;

		while (!forward.isEmpty() && !finished) {

			Node u = forward.deleteMin();
			deleteMinOperations++;

			assert u != null;

			if (u.isSink() ) { continue; } // u is zone centroid node: Do not relax outgoing edges!

			relaxOutgoingEdges(g, tt, u);
		}
	}

	protected void dijkstraOneToTargets(Graph g, TravelTime tt, Node s, int targetsToResolve) {

		assert forward.isEmpty();

		findAtLeastOneTarget = true;

		dist_fw.put(s, startTime);
		relaxOutgoingEdges(g, tt, s);

		assert dist_fw != null;
		assert prev_fw != null;
		assert forward != null;

		boolean finished = false;

		while (!forward.isEmpty() && !finished) {

			Node u = forward.deleteMin();
			deleteMinOperations++;

			assert u != null;

			if (u.isTarget()) {
				foundTargets.add(u);
				if (enoughTargetsFound(targetsToResolve)) {
					return;
				}
			}

			if (u.isSink() ) { continue; } // u is zone centroid node: Do not relax outgoing edges!

			relaxOutgoingEdges(g, tt, u);
		}
		findAtLeastOneTarget = false;
	}

	private boolean enoughTargetsFound(int targetsToResolve) {
		return targetsToResolve >= foundTargets.size();
	}


	protected void dijkstraOneToOne(Graph g, TravelTime tt, Node s, Node t) {

		assert forward.isEmpty();

		dist_fw.put(s, startTime);
		relaxOutgoingEdges(g, tt, s);

		assert dist_fw != null;
		assert prev_fw != null;
		assert forward != null;

		while (!forward.isEmpty()) {

			Node u = forward.deleteMin();
			deleteMinOperations++;

			assert u != null;

			if(u == t) { return; } // target found: stop search

			if (u.isSink() ) { continue; } // u is zone centroid node: Do not relax outgoing edges!

			relaxOutgoingEdges(g, tt, u);
		}
	}

	protected void relaxOutgoingEdges(Graph g, TravelTime tt, Node u) {

		Edge[] edges = g.outgoingEdges(u);

		for (Edge edge : edges) {

			relaxForwardEdge(g, tt, u, edge);
		}
	}


	protected void	relaxForwardEdge(
		Graph g,
		TravelTime tt,
		Node u,
		Edge e
	)
	{
		Node v = g.to(e);

		assert dist_fw.get(u) != null : u;

		float currentTime = dist_fw.get(u);

		Float dist_v = currentTime + tt.travelTime(e, currentTime);

		if (( !dist_fw.containsKey(v) || dist_v < dist_fw.get(v) ) && arrivesEarlyEnough(dist_v))
		{

			if ( !dist_fw.containsKey(v)) {
				forward.add(v, dist_v);
			} else {
				forward.decreaseKey(v, dist_v);
			}

			assert !forward.isEmpty();

			dist_fw.put(v, dist_v);
			prev_fw.put(v, e);
		}
	}

	private boolean arrivesEarlyEnough(Float dist_v) {
		if (findAtLeastOneTarget) {
			return foundTargets.isEmpty() || dist_v < latestArrival;
		}
		return dist_v < latestArrival;
	}


	protected Path unpackPath(
		Graph g,
		Node t,
		HashMap<Node,Edge> prev_fw
	) {

		ArrayList<Link> links = new ArrayList<>();

		Node node = t;

		assert node != null;

		while (prev_fw.get(node) != null) {

			assert node != null;

			Edge e = prev_fw.get(node);

			assert e != null : node;

			links.add((Link)e);

			assert g.from(e) != null;

			node = g.from(e);
		}

		Collections.reverse(links);

		if (links.size() > 0) {
			Path path = new DefaultPath(links, travelTime(t));

			assert path != null;

			return path;
		} else {
			return DefaultPath.invalidPath;
		}
	}

	protected float travelTime(Node n) {

		return dist_fw.get(n) - startTime;
	}

}
