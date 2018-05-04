package edu.kit.ifv.mobitopp.simulation.publictransport.model;

public class StopPair {

	private int from;
	private int to;

	public StopPair(int from, int to) {
		super();
		this.from = from;
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + from;
		result = prime * result + to;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StopPair other = (StopPair) obj;
		if (from != other.from)
			return false;
		if (to != other.to)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StopPair [from=" + from + ", to=" + to + "]";
	}

}