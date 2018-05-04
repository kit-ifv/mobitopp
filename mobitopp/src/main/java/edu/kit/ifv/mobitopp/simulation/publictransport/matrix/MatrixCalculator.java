package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ProfileBuilder;
import edu.kit.ifv.mobitopp.time.Time;

public class MatrixCalculator {

	private final Collection<Zone> zones;
	private final ProfileBuilder builder;
	private final Function<Zone, Stop> stop;
	private final Time day;
	private TravelTimes travelTimes;

	public MatrixCalculator(
			Collection<Zone> zones, ProfileBuilder builder, Function<Zone, Stop> stop,
			Time day) {
		this.zones = zones;
		this.builder = builder;
		this.stop = stop;
		this.day = day;
	}

	private List<Integer> oids() {
		return zones.stream().sorted(comparing(Zone::getOid)).map(Zone::getOid).collect(toList());
	}

	public TravelTimes createMatrices(long hours) {
		travelTimes = new TravelTimes(oids(), hours);
		zones.stream().map(this::toMatrixColumn).forEach(column -> travelTime(column, hours));
		return travelTimes;
	}

	private MatrixColumn toMatrixColumn(Zone target) {
		log(target);
		Profile profile = builder.buildUpTo(stopFor(target));
		save(profile);
		return new MatrixColumn(target, profile);
	}

	private static void log(Zone target) {
		System.out.println(target.getOid());
	}
	
	private void save(Profile profile) {
		travelTimes.add(profile);
	}

	private void travelTime(MatrixColumn column, long hours) {
		for (int hour = 0; hour < hours; hour++) {
			Time startOfSlice = day.plusHours(hour);
			travelTimeTo(column, hour, startOfSlice);
		}
	}

	private void travelTimeTo(MatrixColumn column, int hour, Time startOfSlice) {
		Zone target = column.target;
		Profile profile = column.profile;
		for (Zone source : zones) {
			Duration travelTime = travelTimeFrom(startOfSlice, profile, source);
			travelTimes.update(hour, source, target, travelTime);
		}
	}

	private Duration travelTimeFrom(Time startOfSlice, Profile profile, Zone source) {
		ArrivalTimeSupplier arrival = arrival(profile, source);
		return average(arrival).inHourAfter(startOfSlice);
	}

	TravelTime average(ArrivalTimeSupplier arrival) {
		return new Average(arrival);
	}

	private Earliest arrival(Profile profile, Zone source) {
		return new Earliest(profile, stopFor(source));
	}

	private Stop stopFor(Zone source) {
		return stop.apply(source);
	}
	
	private static class MatrixColumn {

		private final Zone target;
		private final Profile profile;

		public MatrixColumn(Zone target, Profile profile) {
			super();
			this.target = target;
			this.profile = profile;
		}

	}
}
