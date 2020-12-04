package edu.kit.ifv.mobitopp.routing;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueueBasedPQ;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PartitionedGraphFromVisumNetwork 
 	extends GraphFromVisumNetwork 
	implements Graph
{

	protected Node[] allNodes;

	protected final Map<Integer,Area> polygons;

	public Map<Integer,List<Node>> partitionedNodes;

	public PartitionedGraphFromVisumNetwork(VisumRoadNetwork visum) {

		super(visum);

		this.polygons = makePolygons(visum.zones,visum.areas);

		this.partitionedNodes = partitionNodes(this.nodes, this.polygons);

		printParttionedNodes(partitionedNodes);
		log.info("total nodes: " + this.nodes.size()); 
	}

	protected static Map<Integer,Area> makePolygons(
		Map<Integer, VisumZone> zones,
		Map<Integer, VisumSurface> areas
	) {

		HashMap<Integer,Area> data = new HashMap<Integer,Area>();

		for (VisumZone zone: zones.values()) {
			data.put(zone.id,areas.get(zone.areaId).area());
		}

		return data;
	}

	public static Set<Edge> calculateShortestPathTree(Graph graph, Node node) {
		Dijkstra dijkstra = new Dijkstra(new PriorityQueueBasedPQ<Node>());
		Set<Edge> spt  = dijkstra.shortestPathGraphFromAll(graph, node);
		return spt;
	}

	protected Map<Integer,List<Node>>	partitionNodes(Map<Integer,Node> nodes, Map<Integer,Area> polygons) {


		Map<Integer,List<Node>> result = new HashMap<Integer,List<Node>>();

		for (Integer zoneId : polygons.keySet()) {

			result.put(zoneId, new ArrayList<Node>());
		}
		result.put(-1, new ArrayList<Node>());


		for (Node node : nodes.values()) {

			NodeFromVisumNode n = (NodeFromVisumNode) node;

			Point2D p = new Point2D.Double(n.coord().x, n.coord().y);

			boolean found = false;

			for (Integer zoneId : polygons.keySet()) {

				Area zone = polygons.get(zoneId);

				if (zone.contains(p)) {

					result.get(zoneId).add(node);

					found = true;
				}
			}

			if (!found) {
				result.get(-1).add(node);
			}

		}
		
		return result;
	}

	protected void printParttionedNodes(Map<Integer,List<Node>> partitionedNodes) {
		int total = 0;

		for(Integer zoneId : partitionedNodes.keySet()) {

			List<Node> nodes = partitionedNodes.get(zoneId);

			log.info("Zone : " + zoneId + ": " + nodes.size());
			total += nodes.size();

		}

		log.info("total nodes in partition: " + total);
	}

}
