package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class Network {

	public final VisumNetwork visumNetwork;
	public final SimpleRoadNetwork roadNetwork;

	public Network(VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork) {
		super();
		this.visumNetwork = visumNetwork;
		this.roadNetwork = roadNetwork;
	}
	
}
