package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class DepotStation implements Station {

	private final int id = -2;

	@Override
	public int id() {
		return id;
	}

	@Override
	public RelativeTime minimumChangeTime(int id) {
		return RelativeTime.ZERO;
	}

	@Override
	public void add(Stop newStop) {
	}

	@Override
	public void forEach(Consumer<Stop> action) {
	}

	@Override
	public void forEachNode(Consumer<Node> consumer) {
	}

}
