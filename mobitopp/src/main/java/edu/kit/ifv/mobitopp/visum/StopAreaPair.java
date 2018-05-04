package edu.kit.ifv.mobitopp.visum;

public class StopAreaPair {

	private final StopArea fromArea;
	private final StopArea toArea;

	public StopAreaPair(StopArea fromArea, StopArea toArea) {
		super();
		this.fromArea = fromArea;
		this.toArea = toArea;
	}
	
	public StopArea from() {
		return fromArea;
	}
	
	public StopArea to() {
		return toArea;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromArea == null) ? 0 : fromArea.hashCode());
		result = prime * result + ((toArea == null) ? 0 : toArea.hashCode());
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
		StopAreaPair other = (StopAreaPair) obj;
		if (fromArea == null) {
			if (other.fromArea != null) {
				return false;
			}
		} else if (!fromArea.equals(other.fromArea)) {
			return false;
		}
		if (toArea == null) {
			if (other.toArea != null) {
				return false;
			}
		} else if (!toArea.equals(other.toArea)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "StopAreaTupel [fromArea=" + fromArea + ", toArea=" + toArea + "]";
	}

}
