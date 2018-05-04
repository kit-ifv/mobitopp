package edu.kit.ifv.mobitopp.populationsynthesis;

public class CarOwnership {

	private String engine;
	private String ownership;
	private String segment;

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getOwnership() {
		return ownership;
	}

	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [engine=" + engine + ", ownership=" + ownership + ", segment="
				+ segment + "]";
	}

}
