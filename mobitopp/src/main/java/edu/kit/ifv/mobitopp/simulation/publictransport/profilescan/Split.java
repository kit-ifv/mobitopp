package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.time.RelativeTime.of;
import static java.time.temporal.ChronoUnit.HOURS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class Split implements EntrySplitter {

	static final int hoursOfSimulation = 28;
	private final List<EntryAcceptor> parts;
	private final Time start;

	public Split(Time start, List<EntryAcceptor> parts) {
		super();
		this.start = start;
		this.parts = parts;
	}

	@Override
	public Collection<EntryAcceptor> parts() {
		return parts;
	}
	
	@Override
	public Validity validity(int hour) {
		return Accept.perHour(start.plus(of(hour, HOURS)));
	}
	
	@Override
	public Validity validity(Time time) {
		return Accept.perHour(time);
	}

	public static EntrySplitter hourly(Time start) {
		List<EntryAcceptor> parts = new ArrayList<>();
		for (int hour = 0; hour < hoursOfSimulation; hour++) {
			Time current = start.plus(RelativeTime.of(hour, HOURS));
			parts.add(Accept.perHour(current));
		}
		return new Split(start, parts);
	}

}
