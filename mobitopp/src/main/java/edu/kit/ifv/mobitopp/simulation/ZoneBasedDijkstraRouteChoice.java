package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.routing.DefaultPath;
import edu.kit.ifv.mobitopp.routing.Link;
import edu.kit.ifv.mobitopp.routing.GraphFromVisumNetwork;
import edu.kit.ifv.mobitopp.routing.Node;
import edu.kit.ifv.mobitopp.routing.Dijkstra;
import edu.kit.ifv.mobitopp.routing.util.PriorityQueueBasedPQ;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumReader;
import edu.kit.ifv.mobitopp.visum.VisumNetworkReader;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

import java.util.ArrayList;

public class ZoneBasedDijkstraRouteChoice
	implements ZoneBasedRouteChoice {

	private final	GraphFromVisumNetwork graph;
	private final Dijkstra dijkstra = new Dijkstra(new PriorityQueueBasedPQ<>());


	public ZoneBasedDijkstraRouteChoice(String visumFile) {

		VisumReader reader = new VisumReader();
		VisumNetworkReader networkReader = new VisumNetworkReader(reader);

		VisumRoadNetwork network = networkReader.readRoadNetwork(visumFile);

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
	
			System.out.println(source + "(" + sourceZoneId + ")" + " <-> " + target + "(" + targetZoneId + ")");
			return dijkstra.shortestPath(graph, source,target);

		} else {
			return DefaultPath.makePath(new ArrayList<Link>());
		}
	}


} 
