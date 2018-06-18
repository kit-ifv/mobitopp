package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportTrip implements TripIfc {

	private final TripIfc trip;
	private final Optional<PublicTransportRoute> route;
	private final List<PublicTransportLeg> leg;
	private int currentTrip = 0;

	PublicTransportTrip(
			TripIfc trip, Optional<PublicTransportRoute> route, List<PublicTransportLeg> leg) {
		super();
		this.trip = trip;
		this.route = route;
		this.leg = leg;
	}

	public static PublicTransportTrip of(TripIfc trip, Optional<PublicTransportRoute> route) {
		List<PublicTransportLeg> legs = route
				.map(RouteSplitter::splitInParts)
				.orElse(Collections.emptyList());
		return new PublicTransportTrip(trip, route, legs);
	}

	@Override
	public int getOid() {
		return trip.getOid();
	}

	@Override
	public Time calculatePlannedEndDate() {
		return route.map(PublicTransportRoute::arrival).orElse(trip.calculatePlannedEndDate());
	}

	@Override
	public Mode mode() {
		return trip.mode();
	}

	@Override
	public Time startDate() {
		return trip.startDate();
	}

	@Override
	public int plannedDuration() {
		return trip.plannedDuration();
	}

	@Override
	public ActivityIfc previousActivity() {
		return trip.previousActivity();
	}

	@Override
	public ActivityIfc nextActivity() {
		return trip.nextActivity();
	}

	public Optional<PublicTransportLeg> currentLeg() {
		if (leg.isEmpty() || tripDone()) {
			return Optional.empty();
		}
		return Optional.of(leg.get(currentTrip));
	}

	private boolean tripDone() {
		return leg.size() <= currentTrip;
	}

	public void nextLeg() {
		currentTrip++;
	}

	public boolean hasNextLeg() {
		return leg.size() > currentTrip;
	}

	public Optional<PublicTransportLeg> lastLeg() {
		if (leg.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(leg.get(lastElement()));
	}

	private int lastElement() {
		return leg.size() - 1;
	}

	public TripIfc derive(Time currentTime, RouteSearch routeSearch) {
		if (lastLeg().isPresent()) {
			return deriveToEnd(currentTime, routeSearch);
		}
		return this;
	}

	private TripIfc deriveToEnd(Time currentTime, RouteSearch routeSearch) {
		Optional<PublicTransportRoute> tour = currentLeg()
				.flatMap(part -> searchNewTour(routeSearch, part, currentTime));
		return PublicTransportTrip.of(this, tour);
	}

	private Optional<PublicTransportRoute> searchNewTour(
			RouteSearch routeSearch, PublicTransportLeg part, Time currentTime) {
		Stop end = lastLeg().get().end();
		Time partDeparture = part.departure();
		Time searchTime = latestTimeOf(currentTime, partDeparture).plusSeconds(1);
		return routeSearch.findRoute(part.start(), end, searchTime);
	}

	private Time latestTimeOf(Time currentTime, Time partDeparture) {
		if (currentTime.isAfter(partDeparture)) {
			return currentTime;
		}
		return partDeparture;
	}

	@Override
	public String toString() {
		return "PublicTransportTrip [trip=" + trip + ", tour=" + route + "]";
	}

	@Override
	public ZoneAndLocation origin() {
		return trip.origin();
	}

	@Override
	public ZoneAndLocation destination() {
		return trip.destination();
	}
	
	@Override
	public Optional<Time> timeOfNextChange() {
		return currentLeg().map(PublicTransportLeg::departure);
	}

	public FinishedTrip finish(Time currentDate) {
		Statistic statistic = new Statistic();
		return new FinishedPublicTransport(this, currentDate, statistic);
	}

	public FinishedTrip finish(Time currentDate, Events events) {
		Statistic statistic = events.statistic();
		statistic.add(Element.plannedDuration, asTime(plannedDuration()));
		statistic.add(Element.realDuration, duration(currentDate));
		statistic.add(Element.additionalDuration, additionalDuration(currentDate));
		return new FinishedPublicTransport(this, currentDate, statistic);
	}

	private RelativeTime additionalDuration(Time currentDate) {
		return duration(currentDate).minus(asTime(plannedDuration()));
	}

	private RelativeTime duration(Time currentDate) {
		return currentDate.differenceTo(startDate());
	}

	static RelativeTime asTime(int minutes) {
		return RelativeTime.ofMinutes(minutes);
	}

}
