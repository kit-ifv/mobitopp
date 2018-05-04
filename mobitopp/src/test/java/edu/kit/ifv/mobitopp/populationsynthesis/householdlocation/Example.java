package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLink;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;

import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class Example {

	public static final int someLinkId = 1;
	public static final int anotherLinkId = 2;

	public static SimpleRoadNetwork createNetwork() {
		VisumNode someStart = newNode(1);
		VisumNode someEnd = newNode(2);
		VisumNode anotherStart = newNode(3);
		VisumNode anotherEnd = newNode(4);
		VisumTransportSystem car = new VisumTransportSystem("P", "P", "P");
		VisumLink someLink = visumLink()
				.withId(someLinkId)
				.from(someStart)
				.to(someEnd)
				.with(car)
				.withLength(1.0f)
				.build();
		VisumLink anotherLink = visumLink()
				.withId(anotherLinkId)
				.from(anotherStart)
				.to(anotherEnd)
				.with(car)
				.withLength(1.0f)
				.build();
		VisumRoadNetwork visum = visumNetwork()
				.with(car)
				.with(someStart)
				.with(someEnd)
				.with(someLink)
				.with(anotherStart)
				.with(anotherEnd)
				.with(anotherLink)
				.build();
		return new SimpleRoadNetwork(visum);
	}

	private static VisumNode newNode(int id) {
		return visumNode().withId(id).at(id, id).build();
	}

}
