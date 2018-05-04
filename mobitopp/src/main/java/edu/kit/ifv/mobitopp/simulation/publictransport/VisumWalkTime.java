package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPair;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.TransferWalkTime;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.StopArea;
import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTransferWalkTimes;

public class VisumWalkTime implements TransferWalkTime {

	private final Map<StopPair, RelativeTime> duration;

	private VisumWalkTime(Map<StopPair, RelativeTime> pointToPointDuration) {
		super();
		this.duration = pointToPointDuration;
	}

	public static TransferWalkTime from(
			Map<StopAreaPair, VisumPtTransferWalkTimes> walkTimes,
			Collection<VisumPtStopPoint> stopPoints) {
		Map<Integer, List<VisumPtStopPoint>> areaToPoints = areaTo(stopPoints);
		Map<StopPair, RelativeTime> pointToPointDuration = pointToPoint(walkTimes, areaToPoints);
		return new VisumWalkTime(pointToPointDuration);
	}

	private static Map<Integer, List<VisumPtStopPoint>> areaTo(
			Collection<VisumPtStopPoint> stopPoints) {
		return stopPoints
				.stream()
				.collect(groupingBy(point -> point.stopArea.id));
	}

	private static Map<StopPair, RelativeTime> pointToPoint(
			Map<StopAreaPair, VisumPtTransferWalkTimes> walkTimes,
			Map<Integer, List<VisumPtStopPoint>> areaToPoint) {
		Map<StopPair, RelativeTime> pointToPointDuration = new HashMap<>();
		for (Entry<StopAreaPair, VisumPtTransferWalkTimes> areaToArea : walkTimes.entrySet()) {
			StopAreaPair areaPair = areaToArea.getKey();
			StopArea from = areaPair.from();
			StopArea to = areaPair.to();
			for (VisumPtStopPoint fromPoint : areaToPoint.getOrDefault(from.id(), emptyList())) {
				for (VisumPtStopPoint toPoint : areaToPoint.getOrDefault(to.id(), emptyList())) {
					pointToPointDuration.put(pair(fromPoint, toPoint), durationOf(areaToArea));
				}
			}
		}
		return pointToPointDuration;
	}

	private static StopPair pair(VisumPtStopPoint fromPoint, VisumPtStopPoint toPoint) {
		return new StopPair(fromPoint.id, toPoint.id);
	}

	private static RelativeTime durationOf(Entry<StopAreaPair, VisumPtTransferWalkTimes> entry) {
		return RelativeTime.of(entry.getValue().time, SECONDS);
	}

	@Override
	public Optional<RelativeTime> walkTime(Stop from, Stop to) {
		StopPair stopPair = pair(from, to);
		if (duration.containsKey(stopPair)) {
			return Optional.of(duration.get(stopPair));
		}
		return Optional.empty();
	}

	private static StopPair pair(Stop from, Stop to) {
		return new StopPair(from.externalId(), to.externalId());
	}

	@Override
	public RelativeTime minimumChangeTime(int stopId) {
		StopPair inside = inside(stopId);
		if (duration.containsKey(inside)) {
			return duration.get(inside);
		}
		throw new NoChangeTimeAvailable(stopId);
	}

	private StopPair inside(int stopId) {
		return new StopPair(stopId, stopId);
	}

}
