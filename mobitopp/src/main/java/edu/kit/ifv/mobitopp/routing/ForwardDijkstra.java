package edu.kit.ifv.mobitopp.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;

public class ForwardDijkstra implements Router {

	private PriorityQueue<Node> forward;
	private HashMap<Node,Float> dist_fw;
	private HashMap<Node,Edge> prev_fw;

	public ForwardDijkstra(PriorityQueue<Node> queue) {
		this.forward = queue.createNew();
	}


	public Path shortestPath(Graph g, Node s, Node t) {

		assert s != null;
		assert t != null;

		if (s == t) {
			return DefaultPath.emptyPath;
		}
		assert s != t;

		initDataStructures(g);

		dijkstraOneToOne(g, s, t);
		Path p = unpackPath(g, t, prev_fw);

		return p;
	} 

	public Map<Node,Path> shortestPathToAllZones(GraphFromVisumNetwork g, Node s) {

		assert s != null;

		initDataStructures(g);

		dijkstraOneToAll(g, s);

		Map<Node,Path> paths = new HashMap<Node,Path>();

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

	public Map<Node,Path> shortestPathToAll(GraphFromVisumNetwork g, Node s) {

		assert s != null;

		initDataStructures(g);

		dijkstraOneToAll(g, s);

		Map<Node,Path> paths = new HashMap<Node,Path>();

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

	protected void initDataStructures(Graph g) {

		Node[] allNodes = g.nodes();

		dist_fw = new HashMap<Node,Float>(allNodes.length, 1.0f);
		prev_fw = new HashMap<Node,Edge>(allNodes.length, 1.0f);

		forward = forward.createNew();
	}

	protected void dijkstraOneToAll(Graph g, Node s) {

		assert forward.isEmpty();

		dist_fw.put(s, 0.0f);
		relaxOutgoingEdges(g, s);

		assert dist_fw != null;
		assert prev_fw != null;
		assert forward != null;

		boolean finished = false;

System.out.println("starting forwardDijkstra...");

		while (!forward.isEmpty() && !finished) {

			Node u = forward.deleteMin();
			assert u != null;

			if (u.isSink() ) { continue; } // u is zone centroid node: Do not relax outgoing edges!

			relaxOutgoingEdges(g, u);
		}
	}


	protected void dijkstraOneToOne(Graph g, Node s, Node t) {

		assert forward.isEmpty();

		dist_fw.put(s, 0.0f);
		relaxOutgoingEdges(g, s);

		assert dist_fw != null;
		assert prev_fw != null;
		assert forward != null;

		while (!forward.isEmpty()) {

			Node u = forward.deleteMin();
			assert u != null;

			if(u == t) { return; } // target found: stop search

			if (u.isSink() ) { continue; } // u is zone centroid node: Do not relax outgoing edges!

			relaxOutgoingEdges(g, u);
		}
	}

	protected void relaxOutgoingEdges(Graph g, Node u) {

		Edge[] edges = g.outgoingEdges(u);

		for (int i=0; i<edges.length; i++) {

			relaxForwardEdge(g, u, edges[i]);
		}
	}


	protected void	relaxForwardEdge(
		Graph g, 
		Node u, 
		Edge e
	) 
	{
		Node v = g.to(e);

		assert dist_fw.get(u) != null : u;

		Float dist_v = dist_fw.get(u) + g.cost(e);

		if ( !dist_fw.containsKey(v) || dist_v < dist_fw.get(v) ) 
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


	protected Path unpackPath(
		Graph g,
		Node t,
		HashMap<Node,Edge> prev_fw
	) {

		ArrayList<Link> links = new ArrayList<Link>();

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
			Path path = DefaultPath.makePath(links);

			assert path != null;

			return path;
		} else {
			return DefaultPath.invalidPath;
		}
	}

}
