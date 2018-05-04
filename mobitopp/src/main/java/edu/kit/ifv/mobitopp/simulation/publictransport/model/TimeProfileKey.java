package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteDirection;

class TimeProfileKey {

	private final VisumPtLineRouteDirection direction;
	private final String name;
	private final String lineRouteName;
	private final String lineName;

	TimeProfileKey(VisumPtLineRouteDirection direction, String name, String lineRouteName, String lineName) {
		super();
		this.direction = direction;
		this.name = name;
		this.lineRouteName = lineRouteName;
		this.lineName = lineName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((lineName == null) ? 0 : lineName.hashCode());
		result = prime * result + ((lineRouteName == null) ? 0 : lineRouteName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TimeProfileKey other = (TimeProfileKey) obj;
		if (direction != other.direction) {
			return false;
		}
		if (lineName == null) {
			if (other.lineName != null) {
				return false;
			}
		} else if (!lineName.equals(other.lineName)) {
			return false;
		}
		if (lineRouteName == null) {
			if (other.lineRouteName != null) {
				return false;
			}
		} else if (!lineRouteName.equals(other.lineRouteName)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Key [direction=" + direction + ", name=" + name + ", lineRouteName=" + lineRouteName
				+ ", lineName=" + lineName + "]";
	}
}