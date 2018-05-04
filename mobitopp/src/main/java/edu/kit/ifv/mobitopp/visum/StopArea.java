package edu.kit.ifv.mobitopp.visum;

public class StopArea {

	private final int id;

	public StopArea(int id) {
		super();
		this.id = id;
	}
	
	public int id() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		StopArea other = (StopArea) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Area [id=" + id + "]";
	}
}
