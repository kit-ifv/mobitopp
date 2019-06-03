package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.NeighbourhoodCoupler;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPair;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.StopArea;
import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumLinks;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;

class VisumWalkLinks implements NeighbourhoodCoupler {

	static final RelativeTime minimumTime = RelativeTime.of(1, MINUTES);
	private static final float hoursToSeconds = 3600;
	private final Map<StopPair, RelativeTime> duration;

	private VisumWalkLinks(
			Map<StopPair, RelativeTime> duration) {
		super();
		this.duration = duration;
	}

	static NeighbourhoodCoupler from(
			VisumLinks links, Map<Integer, VisumPtStopArea> stopAreas,
			Collection<VisumPtStopPoint> stopPoints, ShortestPathSearch shortestPathSearch,
			VisumLinkType transferWalkType) {
		Map<StopAreaPair, RelativeTime> linkTime = walkTimeBetween(stopAreas, links, transferWalkType);
		Map<StopAreaPair, RelativeTime> searchedTime = searchPathsBetween(stopAreas,
				shortestPathSearch);
		Map<StopAreaPair, RelativeTime> walkTime = Stream
				.of(linkTime, searchedTime)
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.collect(toMap(Entry::getKey, Entry::getValue, VisumWalkLinks::shorterOf));
		Map<Integer, List<VisumPtStopPoint>> areaToPoints = areaTo(stopPoints);
		Map<StopPair, RelativeTime> pointToPointDuration = pointToPoint(areaToPoints, walkTime);
		return new VisumWalkLinks(pointToPointDuration);
	}

	private static RelativeTime shorterOf(RelativeTime first, RelativeTime second) {
		if (0 > first.compareTo(second)) {
			return first;
		}
		return second;
	}

	private static Map<StopPair, RelativeTime> pointToPoint(
			Map<Integer, List<VisumPtStopPoint>> areaToPoints, Map<StopAreaPair, RelativeTime> walkTime) {
		Map<StopPair, RelativeTime> pointToPointDuration = new HashMap<>();
		for (Entry<StopAreaPair, RelativeTime> areaToArea : walkTime.entrySet()) {
			StopAreaPair areaPair = areaToArea.getKey();
			StopArea from = areaPair.from();
			StopArea to = areaPair.to();
			for (VisumPtStopPoint fromPoint : areaToPoints.getOrDefault(from.id(), emptyList())) {
				for (VisumPtStopPoint toPoint : areaToPoints.getOrDefault(to.id(), emptyList())) {
					pointToPointDuration.put(pair(fromPoint, toPoint), durationOf(areaToArea));
				}
			}
		}
		return pointToPointDuration;
	}

	private static StopPair pair(VisumPtStopPoint from, VisumPtStopPoint to) {
		return new StopPair(from.id, to.id);
	}

	private static RelativeTime durationOf(Entry<StopAreaPair, RelativeTime> areaToArea) {
		return areaToArea.getValue();
	}

	private static Map<Integer, List<VisumPtStopPoint>> areaTo(
			Collection<VisumPtStopPoint> stopPoints) {
		return stopPoints
				.stream()
				.collect(groupingBy(point -> point.stopArea.id));
	}

	private static Map<StopAreaPair, RelativeTime> searchPathsBetween(
			Map<Integer, VisumPtStopArea> stopAreas, ShortestPathSearch shortestPathSearch) {
		HashMap<StopAreaPair, RelativeTime> walkTime = new HashMap<>();
		Collection<Node> toAllStopAreas = nodesOf(stopAreas);
		for (VisumPtStopArea start : stopAreas.values()) {
			ShortestPathsToStations result = shortestPathSearch.search(start.node, toAllStopAreas);
			for (VisumPtStopArea end : stopAreas.values()) {
				StopAreaPair startToEnd = keyOf(start, end);
				StopAreaPair endToStart = keyOf(end, start);
				Optional<RelativeTime> duration = duration(result, end);
				duration.ifPresent(time -> walkTime.put(startToEnd, time));
				duration.ifPresent(time -> walkTime.put(endToStart, time));
			}
		}
		return walkTime;
	}

	private static Optional<RelativeTime> duration(
			ShortestPathsToStations result, VisumPtStopArea end) {
		Optional<RelativeTime> duration = result.durationTo(end.node);
		return duration.map(time -> time.compareTo(minimumTime ) > 0 ? time : minimumTime);
	}

	private static Collection<Node> nodesOf(Map<Integer, VisumPtStopArea> stopAreas) {
		Collection<Node> allStopAreaNodes = stopAreas
				.values()
				.stream()
				.map(node -> node.node)
				.filter(node -> node != null)
				.collect(toList());
		return allStopAreaNodes;
	}

	private static Map<StopAreaPair, RelativeTime> walkTimeBetween(
			Map<Integer, VisumPtStopArea> stopAreas, VisumLinks links, VisumLinkType transferWalkType) {
		Map<NodePair, RelativeTime> onLinks = durationOn(links, transferWalkType);
		Map<StopAreaPair, RelativeTime> walkTime = walkTimeBetween(stopAreas, onLinks);
		return walkTime;
	}

	private static Map<StopAreaPair, RelativeTime> walkTimeBetween(
			Map<Integer, VisumPtStopArea> stopAreas, Map<NodePair, RelativeTime> linkDurations) {
		Map<StopAreaPair, RelativeTime> walkTime = new HashMap<>();
		for (VisumPtStopArea start : stopAreas.values()) {
			for (VisumPtStopArea end : stopAreas.values()) {
				add(start, end, linkDurations, walkTime);
			}
		}
		return walkTime;
	}

	private static void add(
			VisumPtStopArea start, VisumPtStopArea end, Map<NodePair, RelativeTime> linkDurations,
			Map<StopAreaPair, RelativeTime> walkTime) {
		if (oneNodeIsMissingIn(start, end)) {
			return;
		}
		NodePair key = nodeKeyOf(start, end);
		if (linkDurations.containsKey(key)) {
			walkTime.put(keyOf(start, end), linkDurations.get(key));
		}
	}

	private static boolean oneNodeIsMissingIn(VisumPtStopArea start, VisumPtStopArea end) {
		return start.node == null || end.node == null;
	}

	private static Map<NodePair, RelativeTime> durationOn(VisumLinks links, VisumLinkType transferWalkType) {
		Map<NodePair, RelativeTime> linkDurations = new HashMap<>();
		for (VisumLink link : links.links.values()) {
			add(link.linkA, linkDurations, transferWalkType);
			add(link.linkB, linkDurations, transferWalkType);
		}
		return linkDurations;
	}

	private static void add(VisumOrientedLink link, Map<NodePair, RelativeTime> linkDurations, VisumLinkType transferWalkType) {
		if (isNoTransfer(link, transferWalkType) || isNoWalkSpeedAvailableOn(link)) {
			return;
		}
		VisumNode from = link.from;
		VisumNode to = link.to;
		int walkSpeed = walkSpeedOf(link);
		float length = link.length;
		float durationInHours = length / walkSpeed;
		RelativeTime duration = RelativeTime.ofSeconds((int) (durationInHours * hoursToSeconds));
		linkDurations.put(nodeTupel(from, to), duration);
	}

	private static int walkSpeedOf(VisumOrientedLink link) {
		return link.attributes.walkSpeed != 0 ? link.attributes.walkSpeed
				: link.linkType.attributes.walkSpeed;
	}

	private static boolean isNoWalkSpeedAvailableOn(VisumOrientedLink link) {
		return link.attributes.walkSpeed <= 0 && link.linkType.attributes.walkSpeed <= 0;
	}

	private static boolean isNoTransfer(VisumOrientedLink link, VisumLinkType transferWalkType) {
		return !transferWalkType.equals(link.linkType);
	}

	private static StopAreaPair keyOf(VisumPtStopArea start, VisumPtStopArea end) {
		StopArea from = new StopArea(start.id);
		StopArea to = new StopArea(end.id);
		return new StopAreaPair(from, to);
	}

	private static NodePair nodeKeyOf(VisumPtStopArea start, VisumPtStopArea end) {
		return nodeTupel(start.node, end.node);
	}

	private static NodePair nodeTupel(VisumNode from, VisumNode to) {
		return new NodePair(from.id(), to.id());
	}

	@Override
	public void addNeighboursshipBetween(Stop start, Stop end) {
		if (contains(start, end)) {
			start.addNeighbour(end, durationFrom(start, end));
		}
		if (contains(end, start)) {
			end.addNeighbour(start, durationFrom(end, start));
		}
	}

	private RelativeTime durationFrom(Stop start, Stop end) {
		return duration.get(pair(start, end));
	}

	private boolean contains(Stop start, Stop end) {
		return duration.containsKey(pair(start, end));
	}

	private static StopPair pair(Stop start, Stop end) {
		return new StopPair(start.externalId(), end.externalId());
	}

	private static class NodePair {

		private final int fromId;
		private final int toId;

		public NodePair(int fromId, int toId) {
			super();
			this.fromId = fromId;
			this.toId = toId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + fromId;
			result = prime * result + toId;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			NodePair other = (NodePair) obj;
			if (fromId != other.fromId) {
				return false;
			}
			if (toId != other.toId) {
				return false;
			}
			return true;
		}
	}
}
