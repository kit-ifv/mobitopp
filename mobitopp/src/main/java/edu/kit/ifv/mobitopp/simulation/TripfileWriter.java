package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.Time;

public class TripfileWriter implements PersonResults {

	private final ResultWriter results;
	private final DateFormat format;
	private final ImpedanceIfc impedance;
	private final TripfileCategories categories;
	private final LocationParser locationParser;

	public TripfileWriter(ResultWriter results, ImpedanceIfc impedance) {
		super();
		this.results = results;
		this.impedance = impedance;
		format = results.dateFormat();
		categories = new TripfileCategories();
		locationParser = new LocationParser();
	}

	private ResultWriter results() {
		return results;
  }

	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip, ActivityIfc activity) {
		ActivityIfc prevActivity = trip.previousActivity();
		assert prevActivity != null;
		
		Location location = activity.location();
		Location prevLocation = prevActivity.location();

		writeTripToFile(person, trip, prevActivity, activity, prevLocation, location);
	}

	private void writeTripToFile(
      Person person,
      FinishedTrip finishedTrip,
      ActivityIfc previousActivity,
      ActivityIfc activity,
			Location location_from,
			Location location_to
	) {

    String sourceZone = null;
    String targetZone = null;

      targetZone = activity.zone().getId();

			sourceZone = finishedTrip.origin().zone().getId();


		float distance = impedance.getDistance(finishedTrip.origin().zone().getOid(),
				finishedTrip.destination().zone().getOid());

			double distance_km = distance/1000.0;

			Household hh = person.household();

		String homeZone = hh.homeZone().getId();


			int duration_trip = finishedTrip.plannedDuration();

			int modeType = finishedTrip.mode().getTypeAsInt();

			String vehicleId = "";

			if (finishedTrip.mode() == Mode.CAR
					|| finishedTrip.mode() == Mode.CARSHARING_STATION
					|| finishedTrip.mode() == Mode.CARSHARING_FREE
			) {
				vehicleId = "" + person.whichCar().id();
			}
			if (finishedTrip.mode() == Mode.PASSENGER) {
				if (person.isCarPassenger()) {
					vehicleId = "" + person.whichCar().id();
				} else {
					vehicleId = "-1";
				}
			}

			int employmentType 		= person.employment().getTypeAsInt();
			int sex								= person.gender().getTypeAsInt();
			int activityType 			= activity.activityType().getTypeAsInt();
			int activityDuration 	= activity.duration();
			int activityNumber 		= activity.getActivityNrOfWeek();

			int personnumber 		= person.getId().getPersonNumber();
			int personOid				= person.getOid();
			int household_oid			= hh.getOid();


			Time begin = previousActivity.calculatePlannedEndDate();
			
			int isStartOfTour =  previousActivity.activityType().isHomeActivity() ? 1 : 0;

		String tripBeginDay = format.asDay(begin);
		String tripBeginTime = format.asTime(begin);

			Time end = finishedTrip.plannedEndDate();

		String tripEndDay = format.asDay(end);
		String tripEndTime = format.asTime(end);

			int tourNumber = -1;
			int isMainActivity = -1;
			int isFirstActivity = -1;
			int tourPurpose = -1;
			
			if (person.activitySchedule() instanceof TourAwareActivitySchedule ) {
				Tour tour  = ((TourAwareActivitySchedule)person.activitySchedule()).correspondingTour(activity);
				tourNumber = tour.tourNumber();
				isMainActivity = activity == tour.mainActivity() ? 1 : 0;
				
				tourPurpose = tour.purpose().getTypeAsInt();
				
				assert tour.contains(activity);
				isFirstActivity = tour.isFirstActivity(activity) ? 1 : 0;
				
				assert isFirstActivity == isStartOfTour;
			}
			Time realEnd = finishedTrip.endDate();
			String realEndDay = format.asDay(realEnd);
			String realEndTime = format.asTime(realEnd);

      CsvBuilder message = new CsvBuilder();
			message.append("W");
			message.append(personnumber);
			message.append(household_oid);
			message.append(personOid);
			message.append(tripBeginDay);
			message.append(activityNumber);
			message.append(tripBeginTime);
			message.append(activityType);
			message.append(modeType);
			message.append(vehicleId);
			message.append(tripEndDay);
			message.append(tripEndTime);
			message.append(distance_km);
			message.append(duration_trip);
			message.append(sourceZone);
			message.append(targetZone);
			message.append(employmentType);
			message.append(homeZone);
			message.append(activityDuration);
			message.append(locationParser.serialiseRounded(location_from));
			message.append(locationParser.serialiseRounded(location_to));
			message.append(sex);
			message.append(tourNumber);
			message.append(isStartOfTour);
			message.append(tourPurpose);
			message.append(isMainActivity);
			message.append(realEndDay);
			message.append(realEndTime);

			if ( finishedTrip.mode() == Mode.CARSHARING_STATION
					|| finishedTrip.mode() == Mode.CARSHARING_FREE
			) {
				message.append( ((CarSharingCar)person.whichCar()).owner().name() );
			}

		results().write(this.categories.result, message.toString());
		CsvBuilder statistics = new CsvBuilder();
		statistics.append(personOid);
		finishedTrip.statistic().forAllElements(statistics::append);
		results().write(categories.ptTimes, statistics.toString());
    }


	@Override
	public void notifyStartActivity(Person person, ActivityIfc activity) {
		Location location = activity.location();
		Zone zone = activity.zone();
		String zoneId = zone.getId();

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

		results().write(this.categories.activity, message.toString());
	}

	@Override
	public void notifyFinishCarTrip(Person person, Car car, TripIfc trip, ActivityIfc activity) {
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
        message.append(person.household().homeZone().getId()).append(";");
        message.append(tour.forLogging()).append(";");
        message.append(tourDestination.getOid()).append(";");
        message.append(tourDestination.getId()).append(";");
        message.append(tourMode).append(";");

        double distanceKm = impedance.getDistance(person.homeZone().getOid(), tourDestination.getOid()) / 1000.0;

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
        message.append(person.household().homeZone().getId()).append(";");
        message.append(tour.forLogging()).append(";");
        message.append(tourMode).append(";");
        message.append(subtour.mode()).append(";");
        message.append(subtour.forLogging()).append(";");

        results().write(this.categories.subtour, message.toString());
    }


	private void writeCarTripToFile(
			Car car,
      TripIfc trip,
      ActivityIfc previousActivity,
      ActivityIfc activity,
			Location location
	) {

		assert trip.mode() == Mode.CAR
					|| trip.mode() == Mode.CARSHARING_STATION
					|| trip.mode() == Mode.CARSHARING_FREE : ("invalid mode: " + trip.mode());


		String targetZone = activity.zone().getId();

		String sourceZone = previousActivity.zone().getId();

		float distance = impedance.getDistance(trip.origin().zone().getOid(),
				trip.destination().zone().getOid());

		double distancekm = distance / 1000.0;

		int durationtrip = trip.plannedDuration();

		int activityType = activity.activityType().getTypeAsInt();

		Time begin = previousActivity.calculatePlannedEndDate();

		String tripBeginDay = format.asDay(begin);
		String tripBeginTime = format.asTime(begin);

		Time end = trip.calculatePlannedEndDate();

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
		message.append(sourceZone);
		message.append(targetZone);
		message.append(location.coordinates());
		message.append(location.roadAccessEdgeId());
		message.append(location.roadPosition());

		results().write(this.categories.car, message.toString());
	}

	@Override
	public void notifySelectCarRoute(Person person, Car car, TripIfc trip, Path route) {

      String sourceZone = trip.origin().zone().getId();
			String targetZone = trip.destination().zone().getId();

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
}
