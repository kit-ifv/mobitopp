package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.time.Time;

public class JourneyBuilder {

	private static final int defaultId = 0;
	private static final Time defaultDay = Data.someTime();
	private static final int defaultCapacity = 0;
	private static final TransportSystem defaultSystem = new TransportSystem("default system");

	private int id;
	private Time day;
	private TransportSystem system;
	private int capacity;

	private JourneyBuilder() {
		super();
		id = defaultId;
		day = defaultDay;
		system = defaultSystem;
		capacity = defaultCapacity;
	}

	public static JourneyBuilder journey() {
		return new JourneyBuilder();
	}

	public JourneyBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public JourneyBuilder at(Time day) {
		this.day = day;
		return this;
	}

	public JourneyBuilder seatsFor(int capacity) {
		this.capacity = capacity;
		return this;
	}

	public JourneyBuilder belongsTo(TransportSystem system) {
		this.system = system;
		return this;
	}

	public ModifiableJourney build() {
		return new DefaultModifiableJourney(id, day, system, capacity);
	}

}
