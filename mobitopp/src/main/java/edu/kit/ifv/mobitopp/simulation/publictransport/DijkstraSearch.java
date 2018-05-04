package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.routing.GraphFromVisumNetwork;
import edu.kit.ifv.mobitopp.routing.Link;
import edu.kit.ifv.mobitopp.routing.Node;
import edu.kit.ifv.mobitopp.routing.NodeFromVisumNode;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.routing.TimeAwareForwardDijkstra;
import edu.kit.ifv.mobitopp.routing.TravelTime;
import edu.kit.ifv.mobitopp.routing.TravelTimeFromGraph;
import edu.kit.ifv.mobitopp.routing.VisumLinkFactory;
import edu.kit.ifv.mobitopp.routing.util.SimplePQ;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

class DijkstraSearch implements ShortestPathSearch {

	private static final float startTime = 0.0f;
	private static final Optional<RelativeTime> unlimited = Optional.empty();
	private static final int allTargets = Integer.MAX_VALUE;
	private final GraphFromVisumNetwork graph;
	private final Optional<RelativeTime> maximumDuration;
	private final int targetsToResolve;

	private DijkstraSearch(GraphFromVisumNetwork graph, Optional<RelativeTime> maximumDuration) {
		this(graph, maximumDuration, allTargets);
	}

	private DijkstraSearch(
			GraphFromVisumNetwork graph, Optional<RelativeTime> maximumDuration, int targetsToResolve) {
		super();
		this.graph = graph;
		this.maximumDuration = maximumDuration;
		this.targetsToResolve = targetsToResolve;
	}

	public static ShortestPathSearch from(
			VisumNetwork network, VisumLinkFactory linkFactory, List<VisumTransportSystem> walking,
			int targetsToResolve, Collection<edu.kit.ifv.mobitopp.network.Node> targets) {
		GraphFromVisumNetwork graph = graphFor(network, linkFactory, walking,
				stationNodesFrom(targets));
		return new DijkstraSearch(graph, unlimited, targetsToResolve);
	}

	public static ShortestPathSearch from(
			VisumNetwork network, VisumLinkFactory walkLinkFactory, RelativeTime maximumDuration,
			List<VisumTransportSystem> walking, int targetsToResolve, Collection<edu.kit.ifv.mobitopp.network.Node> targets) {
		GraphFromVisumNetwork graph = graphFor(network, walkLinkFactory, walking, stationNodesFrom(targets));
		Optional<RelativeTime> duration = Optional.of(maximumDuration);
		return new DijkstraSearch(graph, duration, targetsToResolve);
	}

	private static VisumNodeFactory stationNodesFrom(Collection<edu.kit.ifv.mobitopp.network.Node> targets) {
		return node -> new NodeFromVisumNode(node, targets.contains(node));
	}

	static ShortestPathSearch from(
			VisumNetwork network, VisumLinkFactory linkFactory, List<VisumTransportSystem> walking) {
		GraphFromVisumNetwork graph = graphFor(network, linkFactory, walking, visumNode());
		return unlimited(graph);
	}

	private static ShortestPathSearch unlimited(GraphFromVisumNetwork graph) {
		return new DijkstraSearch(graph, unlimited);
	}

	private static GraphFromVisumNetwork graphFor(
			VisumNetwork network, VisumLinkFactory linkFactory, List<VisumTransportSystem> walking,
			VisumNodeFactory nodeFactory) {
		return new GraphFromVisumNetwork(network, walkLinkValidation(walking),
				walkConnectorValidation(walking), linkFactory, nodeFactory);
	}

	static ShortestPathSearch from(
			VisumNetwork network, VisumLinkFactory linkFactory, RelativeTime maximumDuration,
			List<VisumTransportSystem> walking) {
		GraphFromVisumNetwork graph = graphFor(network, linkFactory, walking, visumNode());
		Optional<RelativeTime> duration = Optional.of(maximumDuration);
		return new DijkstraSearch(graph, duration);
	}

	private static VisumNodeFactory visumNode() {
		return NodeFromVisumNode::new;
	}

	private static Function<VisumOrientedLink, Boolean> walkLinkValidation(
			List<VisumTransportSystem> walking) {
		return link -> containsAtLeastOneOf(walking, link.transportSystems);
	}

	private static boolean containsAtLeastOneOf(
			List<VisumTransportSystem> walking, VisumTransportSystemSet transportSystems) {
		for (VisumTransportSystem transportSystem : walking) {
			if (transportSystems.contains(transportSystem)) {
				return true;
			}
		}
		return false;
	}

	private static Function<VisumConnector, Boolean> walkConnectorValidation(
			List<VisumTransportSystem> walking) {
		return connector -> containsAtLeastOneOf(walking, connector.transportSystems);
	}

	@Override
	public ShortestPathsToStations search(Location location, Collection<edu.kit.ifv.mobitopp.network.Node> toTargets) {
		Link link = graph.linkFor(location.roadAccessEdgeId);
		if (link == null) {
			return new CrowFlyDistance(location, targetsToResolve);
		}
		Node start = link.from();
		Map<Node, Path> search = searchToTargets(start);
		return map(search, toTargets);
	}

	private Map<Node, Path> searchToTargets(Node start) {
		return dijkstra().shortestPathToTargets(graph, travelTime(), start, startTime,
				targetsToResolve);
	}

	private Map<Node, Path> searchFrom(edu.kit.ifv.mobitopp.network.Node from) {
		if (from == null) {
			return Collections.emptyMap();
		}
		Node fromStart = nodeFor(from);
		if (fromStart == null) {
			return Collections.emptyMap();
		}
		return search(fromStart);
	}

	@Override
	public ShortestPathsToStations search(edu.kit.ifv.mobitopp.network.Node start, Collection<edu.kit.ifv.mobitopp.network.Node> toTargets) {
		Map<Node, Path> searchInternal = searchFrom(start);
		return map(searchInternal, toTargets);
	}

	private ShortestPathsToStations map(
			Map<Node, Path> searchInternal, Collection<edu.kit.ifv.mobitopp.network.Node> targets) {
		HashMap<edu.kit.ifv.mobitopp.network.Node, Path> paths = new HashMap<>();
		for (edu.kit.ifv.mobitopp.network.Node visumNode : targets) {
			Node node = nodeFor(visumNode);
			if (node == null || !searchInternal.containsKey(node)) {
				continue;
			}
			Path path = searchInternal.get(node);
			paths.put(visumNode, path);
		}
		return new DijkstraResult(paths);
	}

	private Node nodeFor(edu.kit.ifv.mobitopp.network.Node visumNode) {
		return graph.nodeFor(visumNode.id());
	}

	private Map<Node, Path> search(Node start) {
		return dijkstra().shortestPathToAll(graph, travelTime(), start, startTime);
	}

	private TravelTime travelTime() {
		return new TravelTimeFromGraph(graph);
	}

	private TimeAwareForwardDijkstra dijkstra() {
		float latestArrival = startTime + maximumDurationInMinutes();
		return new TimeAwareForwardDijkstra(new SimplePQ<>(), latestArrival);
	}

	private float maximumDurationInMinutes() {
		return maximumDuration.map(RelativeTime::toMinutes).orElse(Long.MAX_VALUE);
	}

}
