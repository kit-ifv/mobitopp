package edu.kit.ifv.mobitopp.data.local;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class InMemoryMatricesTest {

	private static final int someZone = 0;
	private static final int anotherZone = 1;
	private static final int firstHour = 0;
	private static final int secondHour = 1;

	@Test
	public void travelTimeInFirstHour() {
		InMemoryMatrices matrices = new InMemoryMatrices(travelTimes());

		int origin = someZone;
		int destination = anotherZone;
		Time date = firstHour();
		float travelTime = matrices.getTravelTime(origin, destination, date);

		assertThat(travelTime, is(equalTo((float) firstHour)));
	}

	@Test
	public void travelTimeInSecondHour() {
		InMemoryMatrices matrices = new InMemoryMatrices(travelTimes());

		int origin = someZone;
		int destination = anotherZone;
		Time date = secondHour();
		float travelTime = matrices.getTravelTime(origin, destination, date);

		assertThat(travelTime, is(equalTo((float) secondHour)));
	}

	@Test
	public void travelTimeAtAnotherDay() {
		DayOfWeek weekday = DayOfWeek.TUESDAY;
		InMemoryMatrices matrices = new InMemoryMatrices(travelTimes(weekday));

		int origin = someZone;
		int destination = anotherZone;
		Time date = secondHour(weekday);
		float travelTime = matrices.getTravelTime(origin, destination, date);

		assertThat(travelTime, is(equalTo((float) secondHour)));
	}

	private Map<DayOfWeek, TreeMap<Integer, TravelTimeMatrix>> travelTimes() {
		return travelTimes(DayOfWeek.MONDAY);
	}

	private Map<DayOfWeek, TreeMap<Integer, TravelTimeMatrix>> travelTimes(DayOfWeek weekday) {
		HashMap<DayOfWeek, TreeMap<Integer, TravelTimeMatrix>> day = new HashMap<>();
		day.put(weekday, matricesPerHour());
		return day;
	}

	private TreeMap<Integer, TravelTimeMatrix> matricesPerHour() {
		TreeMap<Integer, TravelTimeMatrix> perHour = new TreeMap<>();
		perHour.put(firstHour, newMatrixFor(firstHour));
		perHour.put(secondHour, newMatrixFor(secondHour));
		return perHour;
	}

	private TravelTimeMatrix newMatrixFor(float hour) {
		TravelTimeMatrix matrix = new TravelTimeMatrix(oids());
		for (ZoneId origin : oids()) {
			for (ZoneId destination : oids()) {
				matrix.set(origin, destination, hour);
			}
		}
		return matrix;
	}

	private List<ZoneId> oids() {
		return asList(newZoneId(someZone), newZoneId(anotherZone));
	}
	
	private ZoneId newZoneId(int id) {
	  return new ZoneId("" + id, id);
	}

	private Time firstHour() {
		return SimpleTime.ofHours(firstHour);
	}

	private Time secondHour() {
		return firstHour().plusHours(1);
	}

	private Time secondHour(DayOfWeek weekday) {
		return firstHour().plusDays(weekday.getTypeAsInt()).plusHours(1);
	}
}
