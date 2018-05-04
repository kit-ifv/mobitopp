package edu.kit.ifv.mobitopp.routing;

import edu.kit.ifv.mobitopp.visum.VisumConnector;

public class LinkFromVisumConnector extends DefaultLink implements Link {

	public LinkFromVisumConnector(
			VisumConnector visumConnector, NodeFromVisumZone from, NodeFromVisumNode to) {
		super(visumConnector.id, from, to, visumConnector.length,
				travelTimeInMinutesOn(visumConnector));
	}

	private static float travelTimeInMinutesOn(VisumConnector visumConnector) {
		return visumConnector.travelTimeInSeconds / 60.0f;
	}

	public LinkFromVisumConnector(
			VisumConnector visumConnector, NodeFromVisumNode from, NodeFromVisumZone to) {
		super(visumConnector.id, from, to, visumConnector.length,
				travelTimeInMinutesOn(visumConnector));
	}

	public int maxSpeed() {
		return 30;
	}

}
