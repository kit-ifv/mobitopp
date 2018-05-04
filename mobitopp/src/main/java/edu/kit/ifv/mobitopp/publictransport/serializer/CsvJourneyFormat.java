package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

class CsvJourneyFormat extends CsvFormat implements JourneyFormat {

	private static final int systemIndex = 3;
	private static final int capacityIndex = 2;
	private static final int dayIndex = 1;
	private static final int idIndex = 0;
	private JourneyFactory factory;

	CsvJourneyFormat(JourneyFactory factory) {
		super();
		this.factory = factory;
	}

	@Override
	public String serialize(Journey journey) {
		StringBuilder serialized = new StringBuilder();
		serialized.append(idOf(journey)).append(separator);
		serialized.append(dayOf(journey)).append(separator);
		serialized.append(capacityOf(journey)).append(separator);
		serialized.append(transportSystemOf(journey));
		return serialized.toString();
	}

	private static int idOf(Journey journey) {
		return journey.id();
	}

	private static long dayOf(Journey journey) {
		return journey.day().toSeconds();
	}

	private static int capacityOf(Journey journey) {
		return journey.capacity();
	}

	private static String transportSystemOf(Journey journey) {
		return journey.transportSystem().code();
	}

	@Override
	public ModifiableJourney deserialize(String serialized) {
		String[] fields = serialized.split(separator);
		int id = idOf(fields);
		Time day = dayOf(fields);
		int capacity = capacityOf(fields);
		TransportSystem system = transportSystemOf(fields);
		return factory.createJourney(id, day, capacity, system);
	}

	private static TransportSystem transportSystemOf(String[] fields) {
		return new TransportSystem(fields[systemIndex]);
	}

	private static int capacityOf(String[] fields) {
		return Integer.parseInt(fields[capacityIndex]);
	}

	private static Time dayOf(String[] fields) {
		return SimpleTime.ofSeconds(Long.parseLong(fields[dayIndex]));
	}

	private static int idOf(String[] fields) {
		return Integer.parseInt(fields[idIndex]);
	}

}
