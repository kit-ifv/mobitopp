package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

class TransferCoupler implements NeighbourhoodCoupler {

	private Map<Stop, Map<Stop, RelativeTime>> walkTimes;

	private TransferCoupler(Map<Stop, Map<Stop, RelativeTime>> walkTimes) {
		this.walkTimes = walkTimes;
	}

	@Override
	public void addNeighboursshipBetween(Stop stop1, Stop stop2) {
		if (walkTimes.containsKey(stop1)) {
			Map<Stop, RelativeTime> times = walkTimes.get(stop1);
			if (times.containsKey(stop2)) {
				RelativeTime walkTime = times.get(stop2);
				stop1.addNeighbour(stop2, walkTime);
			}
		}
		if (walkTimes.containsKey(stop2)) {
			Map<Stop, RelativeTime> times = walkTimes.get(stop2);
			if (times.containsKey(stop1)) {
				RelativeTime walkTime = times.get(stop1);
				stop2.addNeighbour(stop1, walkTime);
			}
		}
	}

	static NeighbourhoodCoupler from(List<StopTransfer> transfers) {
		Map<Stop, Map<Stop, RelativeTime>> walkTimes = new HashMap<>();
		for (StopTransfer stopTransfer : transfers) {
			Stop stop = stopTransfer.stop();
			Map<Stop, RelativeTime> stopToNeighbour = walkTimes.getOrDefault(stop, new HashMap<>());
			stopToNeighbour.put(stopTransfer.neighbour(), stopTransfer.walkTime());
			walkTimes.put(stop, stopToNeighbour);
		}
		return new TransferCoupler(walkTimes);
	}

}
