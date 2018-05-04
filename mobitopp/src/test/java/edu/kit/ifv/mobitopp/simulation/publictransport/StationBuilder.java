package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFromVisum;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.TransferWalkTime;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class StationBuilder {

	static class DummyTransferWalkTime implements TransferWalkTime {

		private static final RelativeTime defaultWalkTime = RelativeTime.ZERO;

		@Override
		public Optional<RelativeTime> walkTime(Stop from, Stop to) {
			return Optional.of(defaultWalkTime);
		}

		@Override
		public RelativeTime minimumChangeTime(int stopId) {
			return defaultWalkTime;
		}
	}

	private static final int defaultId = 0;
	private static final TransferWalkTime defaultWalkTime = new DummyTransferWalkTime();

	private int id;
	private TransferWalkTime walkTime;
	private List<Node> nodes;

	public StationBuilder() {
		super();
		id = defaultId;
		walkTime = defaultWalkTime;
		nodes = emptyList();
	}

	public Station build() {
		return new StationFromVisum(id, walkTime, nodes);
	}

	public StationBuilder with(int id) {
		this.id = id;
		return this;
	}

	public StationBuilder at(List<Node> nodes) {
		this.nodes = nodes;
		return this;
	}

	public StationBuilder at(Node node) {
		this.nodes = Collections.singletonList(node);
		return this;
	}

	public StationBuilder with(TransferWalkTime walkTime) {
		this.walkTime = walkTime;
		return this;
	}

	public static StationBuilder station() {
		return new StationBuilder();
	}

}
