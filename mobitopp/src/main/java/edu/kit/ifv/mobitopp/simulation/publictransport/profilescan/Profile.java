package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class Profile {

	private static final int first = 0;
	private final HashMap<Stop, ArrivalTimeFunction> functions;
	private final Stop target;
	private final Validity validity;

	public Profile(Stop target) {
		this(target, Validity.always);
	}

	Profile(Stop target, Validity validity) {
		super();
		this.target = target;
		this.validity = validity;
		functions = new HashMap<>();
	}

	public static Profile empty(Stop target) {
		return new Profile(target);
	}

	public Stop target() {
		return target;
	}

	public Validity validity() {
		return validity;
	}

	public ArrivalTimeFunction from(Stop key) {
		if (functions.containsKey(key)) {
			return functions.get(key);
		}
		return new ArrivalTimeFunction();
	}

	public ArrivalTimeFunction update(Stop key, ArrivalTimeFunction value) {
		return functions.put(key, value);
	}

	public boolean hasProfileTo(Stop stop) {
		return functions.containsKey(stop);
	}

	public Profile removeChangeTimeAtStart() {
		Profile cleared = new Profile(target);
		for (Entry<Stop, ArrivalTimeFunction> entry : functions.entrySet()) {
			Stop start = entry.getKey();
			ArrivalTimeFunction includingChangeTime = entry.getValue();
			ArrivalTimeFunction clearedChangeTime = includingChangeTime.removeChangeTimeAt(start);
			cleared.update(start, clearedChangeTime);
		}
		return cleared;
	}

	public int size() {
		return functions.size();
	}

	public void saveTo(ProfileWriter writer) {
		functions.forEach(writer::write);
	}

	public void loadFrom(ProfileReader reader) throws IOException {
		while (reader.next()) {
			Stop stop = reader.readStop();
			ArrivalTimeFunction function = reader.readFunction();
			update(stop, function);
		}
	}

	public List<Profile> split(EntrySplitter entrySplitter) {
		ArrayList<Profile> profileParts = new ArrayList<>();
		profileParts.add(new Profile(target, entrySplitter.validity(first)));
		for (Entry<Stop, ArrivalTimeFunction> entry : functions.entrySet()) {
			ArrivalTimeFunction function = entry.getValue();
			List<ArrivalTimeFunction> parts = function.split(entrySplitter);
			for (int index = 0; index < parts.size(); index++) {
				if (profileParts.size() < parts.size()) {
					Validity hour = entrySplitter.validity(profileParts.size());
					profileParts.add(new Profile(target, hour));
				}
				profileParts.get(index).update(entry.getKey(), parts.get(index));
			}
		}
		return profileParts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functions == null) ? 0 : functions.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((validity == null) ? 0 : validity.hashCode());
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
		Profile other = (Profile) obj;
		if (functions == null) {
			if (other.functions != null) {
				return false;
			}
		} else if (!functions.equals(other.functions)) {
			return false;
		}
		if (target == null) {
			if (other.target != null) {
				return false;
			}
		} else if (!target.equals(other.target)) {
			return false;
		}
		if (validity == null) {
			if (other.validity != null) {
				return false;
			}
		} else if (!validity.equals(other.validity)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Profile [functions=" + functions + ", target=" + target + ", validity=" + validity
				+ "]";
	}
}