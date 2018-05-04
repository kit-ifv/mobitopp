package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import edu.kit.ifv.mobitopp.time.Time;

public class TransferEntry {

	private final Time departure;
	private final Time arrivalAtTarget;

	public TransferEntry(Time departure, Time arrivalAtTarget) {
		super();
		this.departure = departure;
		this.arrivalAtTarget = arrivalAtTarget;
	}

	public Time departure() {
		return departure;
	}

	public Time arrival() {
		return arrivalAtTarget;
	}

	@Override
	public String toString() {
		return "TransferEntry [departure=" + departure + ", arrivalAtTarget=" + arrivalAtTarget + "]";
	}

}
