package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.time.Time;

public class Earliest implements ArrivalTimeSupplier {

	private final Profile profile;
	private final Stop source;

	public Earliest(Profile profile, Stop stop) {
		super();
		this.profile = profile;
		this.source = stop;
	}

	@Override
	public Time startingAt(Time departure) {
		return profile.from(source).arrivalFor(departure).orElse(Time.future);
	}

	Optional<Time> arrival(Stop sourceStop, Time departure) {
		return profile.from(sourceStop).arrivalFor(departure);
	}

}
