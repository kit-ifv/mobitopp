package edu.kit.ifv.mobitopp.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;

public class Dijkstra 
	implements Router 
{


	PriorityQueue<Node> forward;
	HashMap<Node,Float> dist_fw;
	HashMap<Node,Edge> prev_fw;

	PriorityQueue<Node> backward;
	HashMap<Node,Float> dist_bw;
	HashMap<Node,Edge> prev_bw;

	double min_dist = Double.POSITIVE_INFINITY;
	Node centralNode = null;

	int deleteMinOperations = 0;


	public Dijkstra(PriorityQueue<Node> queue) {
		this.forward = queue.createNew();
		this.backward = queue.createNew();
	}



	public Path shortestPath(Graph g, Node s, Node t) {

		assert s != null;
		assert t != null;

		if (s == t) {
			return DefaultPath.emptyPath;
		}
		assert s != t;

		initDataStructures(g);

		bidirectionalDijkstra(g, s, t);
		Path p = unpackPath(g, centralNode, prev_fw, prev_bw);

		assert p.size() > 0 || p == DefaultPath.invalidPath : centralNode;

		return p;
	} 

	public Map<Node,Path> shortestPathToAllZones(GraphFromVisumNetwork g, Node s) {

		assert s != null;

		initDataStructures(g);

		forwardDijkstra(g, s);

		Map<Node,Path> paths = new HashMap<Node,Path>();

		for (Node zone : g.zones.values()) {
			if (s == zone) {
				paths.put(zone, DefaultPath.emptyPath);
			} else {
				Path p = unpackPath(g, zone, prev_fw, new HashMap<Node,Edge>(0));

				if (p.isValid()) {
					paths.put(zone, p);
				}
			}
		}

		return paths;
	} 

	public Map<Node,Path> shortestPathToAll(Graph g, Node s) {

System.out.println("starting shortestPathToAll... from " + s);
		assert s != null;

		initDataStructures(g);

		forwardDijkstra(g, s);

		Map<Node,Path> paths = new HashMap<Node,Path>();

		for (Node node : g.nodes()) {

			if (s == node) {
				paths.put(node,DefaultPath.emptyPath);
			} else {
				Path p = unpackPath(g, node, prev_fw, new HashMap<Node,Edge>(0));

				if (p.isValid()) {
					paths.put(node, p);
				}
			}
		}

		return paths;
	} 

	public Map<Node,Path> shortestPathFromAll(Graph g, Node t) {

		assert t != null;

System.out.println("starting shortestPathFromAll... " + t);

		initDataStructures(g);

		backwardDijkstra(g, t);

		Map<Node,Path> paths = new HashMap<Node,Path>();

		for (Node node : g.nodes()) {

			if (t == node) {
				paths.put(node,DefaultPath.emptyPath);
			} else {
				Path p = unpackPath(g, node, new HashMap<Node,Edge>(0), prev_bw);

				if (p.isValid()) {
					paths.put(node, p);
				}
			}
		}

		return paths;
	} 

	public Set<Edge> shortestPathGraphFromAll(Graph g, Node t) {

		assert t != null;

System.out.println("starting shortestPathGraphFromAll... " + t);

		initDataStructures(g);

		backwardDijkstra(g, t);

		return new HashSet<Edge>(prev_bw.values());
	} 

	protected void forwardDijkstra(Graph g, Node s) {

		assert forward.isEmpty();

		dist_fw.put(s, 0.0f);
		relaxOutgoingEdges(g, s);

		assert dist_fw != null;
		assert prev_fw != null;
		assert forward != null;

		boolean finished = false;

		while (!forward.isEmpty() && !finished) {

			finished = forwardDijkstraStep(g, null, true);
		}
	}


	protected void forwardDijkstra(Graph g, Node s, Node t) {

		assert forward.isEmpty();

		dist_fw.put(s, 0.0f);
		relaxOutgoingEdges(g, s);

		assert dist_fw != null;
		assert prev_fw != null;
		assert forward != null;

		boolean finished = false;


		while (!forward.isEmpty() && !finished) {

			finished = forwardDijkstraStep(g, t, false);
		}
	}

	protected void backwardDijkstra(Graph g, Node t) {

		assert t != null;

		assert backward.isEmpty();

		dist_bw.put(t, 0.0f);
		relaxIncomingEdges(g, t);

		assert dist_bw != null;
		assert prev_bw != null;
		assert backward != null;

		boolean finished = false;
		while (!backward.isEmpty() && !finished) {

			finished = backwardDijkstraStep(g, null, true);
		}

	}


	protected void backwardDijkstra(Graph g, Node s, Node t) {

		assert backward.isEmpty();

		dist_bw.put(t, 0.0f);
		relaxIncomingEdges(g, t);

		assert dist_bw != null;
		assert prev_bw != null;
		assert backward != null;

		boolean finished = false;
		while (!backward.isEmpty() && !finished) {

			finished = backwardDijkstraStep(g, s, true);
		}

	}

	protected void bidirectionalDijkstra(Graph g, Node s, Node t) {

		assert backward.isEmpty();
		assert forward.isEmpty();

		dist_fw.put(s, 0.0f);
		relaxOutgoingEdges(g, s);

		dist_bw.put(t, 0.0f);
		relaxIncomingEdges(g, t);

		assert dist_bw != null;
		assert prev_bw != null;
		assert backward != null;

		boolean finished = false;
		boolean dir_fw = true;
		while (!forward.isEmpty() && !backward.isEmpty() && !finished) {

			if (dir_fw) {
				if (!forward.isEmpty()) {
					finished = forwardDijkstraStep(g, t, true);
				}
			} else {
				if (!backward.isEmpty()) {
					finished = backwardDijkstraStep(g, s, true);
				}
			}

			if (min_dist < minKey(forward) + minKey(backward)) {
				finished = true;
			}

			dir_fw = !dir_fw;
		}
	}

	protected double minKey(PriorityQueue<Node> queue) {

		return queue.isEmpty() ? Double.POSITIVE_INFINITY : queue.minPriority();
	}

	protected void initDataStructures(Graph g) {

		Node[] allNodes = g.nodes();

		dist_fw = new HashMap<Node,Float>(allNodes.length, 1.0f);
		dist_bw = new HashMap<Node,Float>(allNodes.length, 1.0f);

		prev_fw = new HashMap<Node,Edge>(allNodes.length, 1.0f);
		prev_bw = new HashMap<Node,Edge>(allNodes.length, 1.0f);

		forward = forward.createNew();
		backward = backward.createNew();

		min_dist = Double.POSITIVE_INFINITY;
		centralNode = null;

	 	deleteMinOperations = 0;
	}


	protected boolean forwardDijkstraStep(Graph g, Node t, boolean oneToAll)
	{
		Node u = forward.deleteMin();
		deleteMinOperations++;

		assert u != null;

		if(u == t && !oneToAll) { return true; }

		if (u.isSink() ) { return false; } // u is zone centroid node : Do not relax outgoing edges!

		relaxOutgoingEdges(g, u);

		return false;
	}

	protected void relaxOutgoingEdges(Graph g, Node u) {

		Edge[] edges = g.outgoingEdges(u);

		for (int i=0; i<edges.length; i++) {

			relaxForwardEdge(g, u, edges[i]);
		}
	}

	protected boolean backwardDijkstraStep(Graph g, Node s, boolean oneToAll)
	{
		Node u = backward.deleteMin();
		deleteMinOperations++;

		assert u != null;

		if(u == s && !oneToAll) { return true; }

		if (u.isSink() ) { return false; } // u is zone centroid node : Do not relax outgoing edges!

		relaxIncomingEdges(g, u);

		return false;
	}

	protected void relaxIncomingEdges(Graph g, Node u) {

		Edge[] edges = g.incomingEdges(u);

		for (int i=0; i<edges.length; i++) {

			relaxBackwardEdge(g, u, edges[i]);
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


			updateMinDistance(v);

		}
	}

	protected void updateMinDistance(Node v) {

		if (dist_fw.containsKey(v) && dist_bw.containsKey(v)) {
				
			double pathLength = dist_fw.get(v) + dist_bw.get(v);

			if (pathLength < min_dist) {
				min_dist = pathLength;
				centralNode = v;
			}

		}
	}

	protected void	relaxBackwardEdge(
		Graph g, 
		Node u, 
		Edge e
	) 
	{
		Node v = g.from(e);

		Float dist_v = dist_bw.get(u) + g.cost(e);

		if ( !dist_bw.containsKey(v) || dist_v < dist_bw.get(v) ) 
		{

			if ( !dist_bw.containsKey(v)) {
				backward.add(v, dist_v);
			} else {
				backward.decreaseKey(v, dist_v);
			}

			assert !backward.isEmpty();

			dist_bw.put(v, dist_v);
			prev_bw.put(v, e);

			updateMinDistance(v);
		}
	}

	protected Path unpackPath(
		Graph g,
		Node centralNode,
		HashMap<Node,Edge> prev_fw,
		HashMap<Node,Edge> prev_bw
	) {

		if (centralNode == null) {
			return DefaultPath.invalidPath;
		}

		ArrayList<Link> links = new ArrayList<Link>();

		Node node = centralNode;

		assert node != null;
		while (prev_fw.get(node) != null) {

			assert node != null;

			Edge e = prev_fw.get(node);

			assert e != null : node;

			links.add((Link)e);

			assert node != null;
			assert e != null;
			assert g.from(e) != null;

			node = g.from(e);
		}

		Collections.reverse(links);

		node = centralNode;
		assert node != null;

		while (prev_bw.get(node) != null) {

			assert node != null;

			Edge e = prev_bw.get(node);

			assert e != null : node;

			links.add((Link)e);

			assert node != null;
			assert e != null;
			assert g.to(e) != null;
			node = g.to(e);
		}



		if (links.size() > 0) {
			Path path = DefaultPath.makePath(links);

			assert links != null;
			assert path != null;

			return path;
		} else {
			return DefaultPath.invalidPath;
		}
	}

}
