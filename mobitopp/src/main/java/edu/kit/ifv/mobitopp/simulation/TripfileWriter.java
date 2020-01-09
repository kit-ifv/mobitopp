package edu.kit.ifv.mobitopp.simulation;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.result.CsvConverter;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.result.TripConverter;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.Time;

public class TripfileWriter implements PersonListener {

	private final ResultWriter results;
	private final DateFormat format;
	private final ImpedanceIfc impedance;
	private final TripfileCategories categories;
	private final LocationParser locationParser;
  private final TripConverter tripConverter;

	public TripfileWriter(ResultWriter results, ImpedanceIfc impedance) {
		super();
		this.results = results;
		this.impedance = impedance;
		format = results.dateFormat();
		categories = new TripfileCategories();
		locationParser = new LocationParser();
		tripConverter = new CsvConverter(impedance, format, locationParser);
	}

	private ResultWriter results() {
		return results;
  }

	@Override
	public void notifyEndTrip(final Person person, final FinishedTrip trip) {
		ActivityIfc prevActivity = trip.previousActivity();
		assert prevActivity != null;

		Consumer<FinishedTrip> tripWriter = finishedTrip -> writeTrip(person, finishedTrip);
		trip.trips().forEach(tripWriter);
		CsvBuilder statistics = new CsvBuilder();
		statistics.append(person.getOid());
		trip.statistic().forAllElements(statistics::append);
		results().write(categories.ptTimes, statistics.toString());
	}

	private void writeTrip(final Person person, final FinishedTrip finishedTrip) {
		String line = tripConverter.convert(person, finishedTrip);
		results().write(this.categories.result, line);
	}

	@Override
	public void notifyStartActivity(Person person, ActivityIfc activity) {
		Location location = activity.location();
		Zone zone = activity.zone();
		String zoneId = zone.getId().getExternalId();

		Time activityStart = activity.startDate();

		String activityStartDay = format.asDay(activityStart);
		String activityStartTime = format.asTime(activityStart);

		CsvBuilder message = new CsvBuilder();
		message.append("A");
		message.append(person.getOid());
		message.append(activityStartDay);
		message.append(activityStartTime);
		message.append(activity.getActivityNrOfWeek());
		message.append(activity.activityType().getTypeAsInt());
		message.append(activity.duration());
		message.append(zoneId);
		message.append(location.coordinates());
    message.append(location.coordinatesP().getX());
    message.append(location.coordinatesP().getY());

		results().write(this.categories.activity, message.toString());
	}

	@Override
	public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {
		ActivityIfc prevActivity = trip.previousActivity();
		assert prevActivity != null;
		Location location = activity.location();
		writeCarTripToFile(car, trip, prevActivity, activity, location);
	}

	
	@Override
  public void writeTourinfoToFile(
      Person person,
      Tour tour,
      Zone tourDestination,
      Mode tourMode
    ){

        StringBuffer message = new StringBuffer();

        // TODO: Wochentag ergänzen!

        message = new StringBuffer();
        message.append("T;");
        message.append(person.getOid()).append(";");
        message.append(person.gender()).append(";");
        message.append(person.age()).append(";");
        message.append(person.employment()).append(";");
        message.append(HouseholdType.type(person.household())).append(";");
        message.append(person.household().getSize()).append(";");
        message.append(person.household().getTotalNumberOfCars()).append(";");
        message.append(person.household().homeZone().getId().getExternalId()).append(";");
        message.append(tour.forLogging()).append(";");
        message.append(tourDestination.getId().getMatrixColumn()).append(";");
        message.append(tourDestination.getId().getExternalId()).append(";");
        message.append(tourMode).append(";");

        ZoneId homeId = person.homeZone().getId();
        ZoneId destinationId = tourDestination.getId();
        double distanceKm = impedance.getDistance(homeId, destinationId) / 1000.0;

        message.append(distanceKm).append(";");

    results().write(this.categories.tour, message.toString());
    }

	@Override
  public void writeSubourinfoToFile(
      Person person,
      Tour tour,
      Subtour subtour,
      Mode tourMode
    ){
        StringBuffer message = new StringBuffer();

        message = new StringBuffer();
        message.append("S;");
        message.append(person.getOid()).append(";");
        message.append(person.gender()).append(";");
        message.append(person.age()).append(";");
        message.append(person.employment()).append(";");
        message.append(HouseholdType.type(person.household())).append(";");
        message.append(person.household().getSize()).append(";");
        message.append(person.household().getTotalNumberOfCars()).append(";");
        message.append(person.household().homeZone().getId().getExternalId()).append(";");
        message.append(tour.forLogging()).append(";");
        message.append(tourMode).append(";");
        message.append(subtour.mode()).append(";");
        message.append(subtour.forLogging()).append(";");

        results().write(this.categories.subtour, message.toString());
    }


	private void writeCarTripToFile(
			Car car,
      FinishedTrip trip,
      ActivityIfc previousActivity,
      ActivityIfc activity,
			Location location
	) {

		assert trip.mode() == StandardMode.CAR
					|| trip.mode() == StandardMode.CARSHARING_STATION
					|| trip.mode() == StandardMode.CARSHARING_FREE : ("invalid mode: " + trip.mode());


		String origin = previousActivity.zone().getId().getExternalId();
		String destination = activity.zone().getId().getExternalId();
    ZoneId originId = trip.origin().zone().getId();
    ZoneId destinationId = trip.destination().zone().getId();
    float distance = impedance.getDistance(originId, destinationId);

		double distancekm = distance / 1000.0;

		int durationtrip = trip.plannedDuration();

		int activityType = activity.activityType().getTypeAsInt();

		Time begin = previousActivity.calculatePlannedEndDate();

		String tripBeginDay = format.asDay(begin);
		String tripBeginTime = format.asTime(begin);

		Time end = trip.plannedEndDate();

		String tripEndDay = format.asDay(end);
		String tripEndTime = format.asTime(end);

		CsvBuilder message = new CsvBuilder();
		message.append("C");
		message.appendWithoutDelimiter(car.statusForLogging());
		message.append(tripBeginDay);
		message.append(tripBeginTime);
		message.append(activityType);
		message.append(tripEndDay);
		message.append(tripEndTime);
		message.append(distancekm);
		message.append(durationtrip);
		message.append(origin);
		message.append(destination);
		message.append(location.coordinates());
		message.append(location.roadAccessEdgeId());
		message.append(location.roadPosition());

		results().write(this.categories.car, message.toString());
	}

	@Override
	public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {

      String sourceZone = trip.origin().zone().getId().getExternalId();
			String targetZone = trip.destination().zone().getId().getExternalId();

			int personoid				= person.getOid();
			int carid 					= car.id();

			Time begin = trip.startDate();
		String tripBeginDay = format.asDay(begin);
		String tripBeginTime = format.asTime(begin);

			Time end = trip.calculatePlannedEndDate();
		String tripEndDay = format.asDay(end);
		String tripEndTime = format.asTime(end);


		CsvBuilder message = new CsvBuilder();
		message.append("R");
		message.append(personoid);
		message.append(carid);
		message.append(sourceZone);
		message.append(targetZone);
		message.append(tripBeginDay);
		message.append(tripBeginTime);
		message.append(tripEndDay);
		message.append(tripEndTime);
		message.append(route);

		results().write(this.categories.route, message.toString());
	}
	
	@Override
	public void notifyStateChanged(StateChange change) {
		CsvBuilder message = new CsvBuilder();
		message.append(change.person().getOid());
		message.append(format.asDay(change.date()));
		message.append(format.asTime(change.date()));
		message.append(change.previous());
		message.append(change.next());
		results().write(this.categories.stateChange, message.toString());
	}

  @Override
  public void notifyFinishSimulation() {
  }

}
