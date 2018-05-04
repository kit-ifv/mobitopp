package edu.kit.ifv.mobitopp.routing;

import java.util.Map;

public class TravelTimeFromList implements TravelTime {

	private final Map<Link, Float> travelTimes;

	public TravelTimeFromList(Map<Link, Float> travelTimes) {
		this.travelTimes = travelTimes;
	}

	public float travelTime(Edge e, float currentTime) {
		return this.travelTimes.get(e);
	}

}
