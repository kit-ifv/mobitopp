package edu.kit.ifv.mobitopp.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.routing.util.FibonacciHeap;

public class DijkstraFibonacci implements Router {

	public DijkstraFibonacci() {
		super();
	}

	public Path shortestPath(Graph g, Node s, Node t) {

		assert s != null;
		assert t != null;

		if (s == t) {
			return unpackPath(g, s, t, new HashMap<Node,Edge>(0));
		}
		assert s != t;

		Node[] allNodes = g.nodes();

		HashMap<Node,Float> dist = new HashMap<Node,Float>(allNodes.length, 1.0f);
		HashMap<Node,Edge> previous = new HashMap<Node,Edge>(allNodes.length, 1.0f);

		FibonacciHeap<Node> queue = new FibonacciHeap<Node>();
		Map<Node,FibonacciHeap.Entry<Node>> enqueuedNodes = new HashMap<Node,FibonacciHeap.Entry<Node>>();

		assert queue.isEmpty();

		dist.put(s, 0.0f);
		FibonacciHeap.Entry<Node> es = queue.enqueue(s, 0.0f);
		enqueuedNodes.put(s,es);

		assert dist != null;
		assert previous != null;
		assert queue != null;

		boolean found = false;

		while (!queue.isEmpty() && !found) {
			FibonacciHeap.Entry<Node> u_entry = queue.dequeueMin();
			Node u = u_entry.getValue();
			enqueuedNodes.remove(u);
			Float dist_u = dist.get(u);

			assert u != null;
			assert dist_u != null;

			if(u == t) { found = true; }

			Edge[] edges = g.outgoingEdges(u);

			for (int i=0; i<edges.length; i++) {

				Edge e = edges[i];

				assert e != null;

				Node v = g.to(e);

				assert v != null;

				Float dist_v = dist_u + g.cost(e);

				assert dist_v != null;

				if ( !dist.containsKey(v) || dist_v < dist.get(v) ) 
				{

					if ( !dist.containsKey(v)) {
						FibonacciHeap.Entry<Node> ev = queue.enqueue(v, dist_v);
						enqueuedNodes.put(v,ev);
					} else {
						FibonacciHeap.Entry<Node> ev = enqueuedNodes.get(v);
						queue.decreaseKey(ev, dist_v);
					}

					assert !queue.isEmpty();

					dist.put(v, dist_v);
					previous.put(v, e);
				}
			}
		}

		assert previous.containsKey(t) : ("s=" + s + ", t=" + t);

		return unpackPath(g, s, t, previous);
	}

	protected Path unpackPath(
		Graph g,
		Node s,
		Node t,
		HashMap<Node,Edge> previous
	) {

		ArrayList<Link> links = new ArrayList<Link>();

		Node node = t;

		assert node != null;
		while (node != s) {

			assert node != null;

			Edge e = previous.get(node);

			assert e != null;

			links.add((Link)e);

			assert node != null;
			assert e != null;
			assert g.from(e) != null;

			node = g.from(e);
		}


		Collections.reverse(links);

		Path path = DefaultPath.makePath(links);

		assert links != null;
		assert path != null;

		return path;
	}

}
