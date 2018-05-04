package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class DummyStore implements Store {

	private final Map<Stop, Profile> profiles;

	public DummyStore() {
		super();
		profiles = new HashMap<>();
	}

	@Override
	public void save(Profile profile) {
		profiles.put(profile.target(), profile);
	}

	@Override
	public Profile profileTo(Stop target, Time time) {
		return profiles.get(target);
	}
}