package edu.kit.ifv.mobitopp.data.local;

import java.util.Map;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class InMemoryMatrices {

	private final Map<DayOfWeek, TreeMap<Integer, TravelTimeMatrix>> matrices;

	public InMemoryMatrices(Map<DayOfWeek, TreeMap<Integer, TravelTimeMatrix>> matrices) {
		this.matrices = matrices;
	}

	public float getTravelTime(int origin, int destination, Time date) {
		DayOfWeek dayOfWeek = date.weekDay();
		int hour = date.getHour();
		return matrices.get(dayOfWeek).floorEntry(hour).getValue().get(origin, destination);
	}

}
