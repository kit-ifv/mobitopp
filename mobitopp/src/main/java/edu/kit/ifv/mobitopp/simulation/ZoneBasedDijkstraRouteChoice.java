package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;

import edu.kit.ifv.mobitopp.routing.DefaultPath;
import edu.kit.ifv.mobitopp.routing.Dijkstra;
import edu.kit.ifv.mobitopp.routing.GraphFromVisumNetwork;
import edu.kit.ifv.mobitopp.routing.Link;
import edu.kit.ifv.mobitopp.routing.Node;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.routing.util.PriorityQueueBasedPQ;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZoneBasedDijkstraRouteChoice
	implements ZoneBasedRouteChoice {

	private final	GraphFromVisumNetwork graph;
	private final Dijkstra dijkstra = new Dijkstra(new PriorityQueueBasedPQ<>());


	public ZoneBasedDijkstraRouteChoice(VisumRoadNetwork network) {
		this.graph = new GraphFromVisumNetwork(network);
	}


	public Path selectRoute(
					Time date,
					int sourceZoneId,
					int targetZoneId
	) {

		if (sourceZoneId != targetZoneId) {

			Node source = graph.getNodeForZone(sourceZoneId);
			Node target = graph.getNodeForZone(targetZoneId);
	
			log.info(source + "(" + sourceZoneId + ")" + " <-> " + target + "(" + targetZoneId + ")");
			return dijkstra.shortestPath(graph, source,target);

		} else {
			return DefaultPath.makePath(new ArrayList<Link>());
		}
	}


} 
