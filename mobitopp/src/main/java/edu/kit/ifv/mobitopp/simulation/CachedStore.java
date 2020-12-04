package edu.kit.ifv.mobitopp.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Store;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CachedStore implements Store {

	private final Store store;
	private final Map<Key, Profile> profiles;

	public CachedStore(Store store) {
		super();
		this.store = store;
		profiles = new HashMap<>();
	}

	@Override
	public void save(Profile profile) {
		store.save(profile);
	}

	@Override
	public Profile profileTo(Stop target, Time time) {
		Key key = keyOf(target, time);
		if (profiles.containsKey(key)) {
			return profiles.get(key);
		}
		Profile profile = store.profileTo(target, time);
		profiles.put(key, profile);
		return profile;
	}

	public void cleanBefore(Time time) {
		log.info("Clean profile cache");
		clean(profilesBefore(time));
		triggerGc();
	}

	private List<Key> profilesBefore(Time time) {
		List<Key> remove = new LinkedList<>();
		for (Key key : profiles.keySet()) {
			if (key.isBefore(time)) {
				remove.add(key);
			}
		}
		return remove;
	}

	private void clean(List<Key> remove) {
		for (Key key : remove) {
			profiles.remove(key);
		}
	}

	private void triggerGc() {
		System.gc();
	}

	private static Key keyOf(Stop target, Time time) {
		int hour = time.getHour();
		return new Key(target, hour);
	}

	private static class Key {

		private final Stop stop;
		private final int hour;

		private Key(Stop stop, int hour) {
			super();
			this.stop = stop;
			this.hour = hour;
		}

		public boolean isBefore(Time limit) {
			return hour < (limit.getHour());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + hour;
			result = prime * result + ((stop == null) ? 0 : stop.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Key other = (Key) obj;
			if (hour != other.hour) {
				return false;
			}
			if (stop == null) {
				if (other.stop != null) {
					return false;
				}
			} else if (!stop.equals(other.stop)) {
				return false;
			}
			return true;
		}

	}

}
