package edu.kit.ifv.mobitopp.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteElement;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class SimplePtNetwork 
	extends SimpleGraph
	implements Graph
{

	private static final long serialVersionUID = 1L;
	
	public final Map<Integer, SimpleNode> stops;
	protected Map<VisumTransportSystem,Map<String,List<SimpleEdge>>> lines;
	protected Map<VisumTransportSystem,Map<String,List<SimpleNode>>> nodes;
	protected Map<VisumTransportSystem,Map<String,List<SimpleNode>>> stopNodes;

	public SimplePtNetwork(VisumNetwork visum) {
		super(visum.transportSystems);
		System.out.println("Parsing stops...");
		this.stops = Collections.unmodifiableMap(parseStops(visum.ptStops));

		System.out.println("Parsing lines...");
		parseLineRoutes(visum.ptLineRoutes);
	}

	public Collection<VisumTransportSystem> transportSystems() {

		return lines.keySet();
	}

	public Map<String,List<SimpleEdge>> linesByTransportSystem(VisumTransportSystem system) {

		if (lines.containsKey(system)) {
			return lines.get(system);
		} else {
			return new HashMap<String,List<SimpleEdge>>();
		}
	}

	public Map<String,List<SimpleNode>> stopsByTransportSystem(VisumTransportSystem system) {

		if (stopNodes.containsKey(system)) {
			return stopNodes.get(system);
		} else {
			return new HashMap<String,List<SimpleNode>>();
		}
	}


	protected Map<Integer, SimpleNode> parseStops(
		Map<Integer, VisumPtStop> ptStops
	) {

		Map<Integer, SimpleNode> result = new HashMap<Integer, SimpleNode>();

		for (Integer id : ptStops.keySet()) {

			VisumPtStop vStop = ptStops.get(id);

			result.put(id, makeNode(id, vStop.coord));
		}

		return result;
	}

	protected void parseLineRoutes(
		Map<String, VisumPtLineRoute> ptLineRoutes
	) {

		int i=0;

		Map<VisumTransportSystem,Map<String,List<SimpleEdge>>> edgesByRoute 
			= new HashMap<VisumTransportSystem,Map<String,List<SimpleEdge>>>();

		Map<VisumTransportSystem,Map<String,List<SimpleNode>>> stopsByRoute
			= new HashMap<VisumTransportSystem,Map<String,List<SimpleNode>>>();

		Map<VisumTransportSystem,Map<String,List<SimpleNode>>> nodesByRoute
			= new HashMap<VisumTransportSystem,Map<String,List<SimpleNode>>>();

		for (String id : ptLineRoutes.keySet()) {

			VisumPtLineRoute vRoute = ptLineRoutes.get(id);

			VisumTransportSystem transportSystem = vRoute.line.transportSystem;

			if (!edgesByRoute.containsKey(transportSystem)) {
				edgesByRoute.put(transportSystem, new HashMap<String,List<SimpleEdge>>());
				stopsByRoute.put(transportSystem, new HashMap<String,List<SimpleNode>>());
				nodesByRoute.put(transportSystem, new HashMap<String,List<SimpleNode>>());
			}

			List<SimpleNode> routeNodes = new ArrayList<SimpleNode>();
			List<SimpleNode> routeStops = new ArrayList<SimpleNode>();

			for (VisumPtLineRouteElement element : vRoute.elements.values()) {
				if (element.stopPoint != null) {
					SimpleNode node = makeNode(element.stopPoint.stopArea.node);
					routeNodes.add(node);
					routeStops.add(node);
				} else {	
					SimpleNode node = makeNode(element.node);
					routeNodes.add(node);
				}
			}
			List<SimpleEdge> route = new ArrayList<SimpleEdge>();

			SimpleNode previousNode = routeNodes.get(0);

			for (SimpleNode node : routeNodes) {

				if (node != routeNodes.get(0)) {

					SimpleEdge edge = makeEdge(i++, previousNode, node);

					route.add(edge);

					previousNode = node;
				}
			}

			edgesByRoute.get(transportSystem).put(id, route);
			nodesByRoute.get(transportSystem).put(id, routeNodes);
			stopsByRoute.get(transportSystem).put(id, routeStops);
		}

		this.lines = Collections.unmodifiableMap(edgesByRoute);
		this.nodes = Collections.unmodifiableMap(nodesByRoute);
		this.stopNodes = Collections.unmodifiableMap(stopsByRoute);
	}

}
