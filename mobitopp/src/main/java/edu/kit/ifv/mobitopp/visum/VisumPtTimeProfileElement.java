package edu.kit.ifv.mobitopp.visum;

import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class VisumPtTimeProfileElement {

	public final VisumPtLineRoute route;

	public final String profileName;
	public final int index;
	public final int lrElemIndex;

	public final VisumPtLineRouteElement lineRouteElement;

	public final boolean enter;
	public final boolean leave;

	public final int arrival;
	public final int departure;


	public VisumPtTimeProfileElement(
		VisumPtLineRoute route,
		String profileName,
		int index,
		int lrElemIndex,
		VisumPtLineRouteElement lineRouteElement,
		boolean enter,
		boolean leave,
		int arrival,
		int departure
	) {
		this.route = route;
		this.profileName = profileName;
		this.index = index;
		this.lrElemIndex = lrElemIndex;
		this.lineRouteElement = lineRouteElement;
		this.enter = enter;
		this.leave = leave;
		this.arrival = arrival;
		this.departure = departure;
	}


	public String toString() {

		return "VisumPtTimeProfileElement("
						+ route + ","
						+ profileName + ","
						+ index + ","
						+ lrElemIndex + ","
						+ enter + ","
						+ leave + ","
						+ arrivalHHMMSS() + ","
						+ departureHHMMSS() + ")";
	}

	public String arrivalHHMMSS() {
		Time arrival = SimpleTime.ofSeconds(this.arrival);
		return new DateFormat().asTime(arrival);
	}

	public String departureHHMMSS() {
		Time departure = SimpleTime.ofSeconds(this.departure);
		return new DateFormat().asTime(departure);
	}


}
