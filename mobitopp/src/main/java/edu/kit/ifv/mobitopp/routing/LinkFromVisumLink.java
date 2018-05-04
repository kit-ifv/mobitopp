package edu.kit.ifv.mobitopp.routing;

import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;

public class LinkFromVisumLink extends DefaultLink implements Link {

	private final VisumOrientedLink visumLink;

	public LinkFromVisumLink(
			VisumOrientedLink visumLink, NodeFromVisumNode from, NodeFromVisumNode to) {
		this(visumLink, from, to, visumLink.length, travelTimeOn(visumLink));
	}

	public LinkFromVisumLink(
			VisumOrientedLink visumLink, NodeFromVisumNode from, NodeFromVisumNode to, float distance,
			float travelTime) {
		super(visumLink.id, from, to, distance, travelTime);

		this.visumLink = visumLink;
	}

	private static Float travelTimeOn(VisumOrientedLink visumLink) {
		return 60 * (visumLink.length / maxSpeed(visumLink));
	}

	private static int maxSpeed(VisumOrientedLink visumLink) {
		return visumLink.attributes.freeFlowSpeedCar;
	}
	
	public int maxSpeed() {
		return visumLink.attributes.freeFlowSpeedCar;
	}

	public String toString() {
		String s = "(" + "L" + visumLink.id + super.toString() + ")";
		return s;
	}

}
