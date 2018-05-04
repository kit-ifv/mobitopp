package edu.kit.ifv.mobitopp.simulation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Store;
import edu.kit.ifv.mobitopp.time.Time;

public class InMemory implements Store {

	private final Map<Stop, Profile> profiles;
	
	public InMemory() {
		super();
		profiles = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public void save(Profile profile) {
		this.profiles.put(profile.target(), profile);
	}

	@Override
	public Profile profileTo(Stop target, Time time) {
		if (profiles.containsKey(target)) {
			return profiles.get(target);
		}
		throw new ProfileMissing(target);
	}

}
