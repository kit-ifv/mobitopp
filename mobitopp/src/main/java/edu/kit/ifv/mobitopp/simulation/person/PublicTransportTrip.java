package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportTrip extends BaseTrip implements TripIfc {

	private final Optional<PublicTransportRoute> route;
	private final List<PublicTransportLeg> legs;
	private int currentLeg = 0;
  private PublicTransportBehaviour publicTransportBehaviour;

  PublicTransportTrip(
      TripData trip, SimulationPerson person,
      PublicTransportBehaviour publicTransportBehaviour,
      Optional<PublicTransportRoute> route, List<PublicTransportLeg> legs) {
	  super(trip, person);
	  this.route = route;
	  this.legs = legs;
    this.publicTransportBehaviour = publicTransportBehaviour;
  }

  public static PublicTransportTrip of(
      TripData trip, SimulationPerson person, PublicTransportBehaviour publicTransportBehaviour,
      Optional<PublicTransportRoute> route) {
		List<PublicTransportLeg> legs = route
				.map(RouteSplitter::splitInParts)
				.orElse(Collections.emptyList());
		return new PublicTransportTrip(trip, person, publicTransportBehaviour, route, legs);
	}

	@Override
	public Time calculatePlannedEndDate() {
		return route.map(PublicTransportRoute::arrival).orElse(super.calculatePlannedEndDate());
	}

	public Optional<PublicTransportLeg> currentLeg() {
		if (legs.isEmpty() || tripDone()) {
			return Optional.empty();
		}
		return Optional.of(legs.get(currentLeg));
	}

	private boolean tripDone() {
		return legs.size() <= currentLeg;
	}

	public void nextLeg() {
		currentLeg++;
	}

	public boolean hasNextLeg() {
		return legs.size() > currentLeg;
	}

	public Optional<PublicTransportLeg> lastLeg() {
		if (legs.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(legs.get(lastElement()));
	}

	private int lastElement() {
		return legs.size() - 1;
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
		return PublicTransportTrip.of(this, person(), publicTransportBehaviour, tour);
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
	public Optional<Time> timeOfNextChange() {
		return currentLeg().map(PublicTransportLeg::departure);
	}

	public FinishedTrip finish(Time currentDate) {
		Statistic statistic = new Statistic();
		return new FinishedPublicTransport(this, currentDate, statistic);
	}

	public FinishedTrip finish(Time currentDate, Events events) {
	  leaveLastLeg();
		Statistic statistic = createStatistic(currentDate, events);
		return new FinishedPublicTransport(this, currentDate, statistic);
	}

  private void leaveLastLeg() {
    lastLeg().ifPresent(part -> publicTransportBehaviour.leaveWaitingArea(person(), part.end()));
  }

  private Statistic createStatistic(Time currentDate, Events events) {
    Statistic statistic = events.statistic();
		statistic.add(Element.plannedDuration, asTime(plannedDuration()));
		statistic.add(Element.realDuration, duration(currentDate));
		statistic.add(Element.additionalDuration, additionalDuration(currentDate));
    return statistic;
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

  @Override
  public String toString() {
    return "PublicTransportTrip [trip=" + super.toString() + ", tour=" + route + "]";
  }
}
