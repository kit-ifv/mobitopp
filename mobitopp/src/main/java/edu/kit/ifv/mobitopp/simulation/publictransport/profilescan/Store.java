package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public interface Store {

	static Store nothing = new Store() {

		@Override
		public void save(Profile profile) {
		}

		@Override
		public Profile profileTo(Stop target, Time time) {
			return Profile.empty(target);
		}
	};

	void save(Profile profile);

	Profile profileTo(Stop target, Time time);

}
