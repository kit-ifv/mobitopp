package edu.kit.ifv.mobitopp.util.dataexport;

public class OdRelation {

	private final String origin;
	private final String destination;
	private final int departure;

	public OdRelation(String origin, String destination, int departure) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.departure = departure;
	}

	public String origin() {
		return origin;
	}

	public String destination() {
		return destination;
	}

	public int departure() {
		return departure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + departure;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
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
		OdRelation other = (OdRelation) obj;
		if (departure != other.departure) {
			return false;
		}
		if (destination == null) {
			if (other.destination != null) {
				return false;
			}
		} else if (!destination.equals(other.destination)) {
			return false;
		}
		if (origin == null) {
			if (other.origin != null) {
				return false;
			}
		} else if (!origin.equals(other.origin)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "OdRelation [origin=" + origin + ", destination=" + destination + ", departure="
				+ departure + "]";
	}

}
