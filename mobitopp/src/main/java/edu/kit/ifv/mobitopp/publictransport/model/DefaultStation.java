package edu.kit.ifv.mobitopp.publictransport.model;

import java.util.List;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class DefaultStation extends BaseStation implements Station {

	public DefaultStation(int id, List<Node> nodes) {
		super(id, nodes);
	}

	@Override
	public RelativeTime minimumChangeTime(int id) {
		return RelativeTime.INFINITE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id();
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
		DefaultStation other = (DefaultStation) obj;
		if (id() != other.id()) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DefaultStation [id=" + id() + "]";
	}

}
