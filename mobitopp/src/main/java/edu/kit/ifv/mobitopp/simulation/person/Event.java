package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent;
import edu.kit.ifv.mobitopp.time.Time;

public class Event {

	private final PassengerEvent type;
	private final Time time;
	private final Journey journey;

	public Event(PassengerEvent type, Time time, Journey journey) {
		super();
		this.type = type;
		this.time = time;
		this.journey = journey;
	}

	public PassengerEvent type() {
		return type;
	}

	public Time time() {
		return time;
	}

	public Journey journey() {
		return journey;
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", time=" + time + ", journey=" + journey + "]";
	}

}