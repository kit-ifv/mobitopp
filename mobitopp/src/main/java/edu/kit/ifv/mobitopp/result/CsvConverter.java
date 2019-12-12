package edu.kit.ifv.mobitopp.result;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.simulation.tour.TourAwareActivitySchedule;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.Time;

public class CsvConverter implements TripConverter {

  private final ImpedanceIfc impedance;
  private final DateFormat format;
  private final LocationParser locationParser;

  public CsvConverter(ImpedanceIfc impedance, DateFormat format, LocationParser locationParser) {
    super();
    this.impedance = impedance;
    this.format = format;
    this.locationParser = locationParser;
  }

	@Override
	public String convert(final Person person, final FinishedTrip finishedTrip) {
  	ActivityIfc previousActivity = finishedTrip.previousActivity();
  	ActivityIfc nextActivity = finishedTrip.nextActivity();
  	Location location_from = previousActivity.location();
  	Location location_to = nextActivity.location();
    String origin = finishedTrip.origin().zone().getId().getExternalId();
    String destination = nextActivity.zone().getId().getExternalId();
    ZoneId originId = finishedTrip.origin().zone().getId();
    ZoneId destinationId = finishedTrip.destination().zone().getId();
    float distance = impedance.getDistance(originId, destinationId);

    double distance_km = distance / 1000.0;

    Household hh = person.household();

    String homeZone = hh.homeZone().getId().getExternalId();

    int duration_trip = finishedTrip.plannedDuration();

    String modeType = finishedTrip.mode().forLogging();
    String mainMode = finishedTrip.mode().mainMode().forLogging();
    String vehicleId = finishedTrip.vehicleId().orElse("");

    int employmentType = person.employment().getTypeAsInt();
    int sex = person.gender().getTypeAsInt();
    int activityType = nextActivity.activityType().getTypeAsInt();
    int activityDuration = nextActivity.duration();
    int activityNumber = nextActivity.getActivityNrOfWeek();

    int personnumber = person.getId().getPersonNumber();
    int personOid = person.getOid();
    int household_oid = hh.getOid();

    Time begin = previousActivity.calculatePlannedEndDate();

    int isStartOfTour = previousActivity.activityType().isHomeActivity() ? 1 : 0;

    String tripBeginDay = format.asDay(begin);
    String tripBeginTime = format.asTime(begin);

    Time end = finishedTrip.plannedEndDate();

    String tripEndDay = format.asDay(end);
    String tripEndTime = format.asTime(end);

    int tourNumber = -1;
    int isMainActivity = -1;
    int isFirstActivity = -1;
    int tourPurpose = -1;

    if (person.activitySchedule() instanceof TourAwareActivitySchedule) {
      Tour tour = ((TourAwareActivitySchedule) person.activitySchedule())
          .correspondingTour(nextActivity);
      tourNumber = tour.tourNumber();
      isMainActivity = nextActivity == tour.mainActivity() ? 1 : 0;

      tourPurpose = tour.purpose().getTypeAsInt();

      assert tour.contains(nextActivity);
      isFirstActivity = tour.isFirstActivity(nextActivity) ? 1 : 0;

      assert isFirstActivity == isStartOfTour;
    }
    Time Actbegin = nextActivity.startDate();
    String activityStartTime = format.asTime(Actbegin);
    String activityStartDay = format.asDay(Actbegin);
    int previousActivityType = previousActivity.activityType().getTypeAsInt();
    Time prevActbegin = previousActivity.startDate();
    String previousActivityStartTime = format.asTime(prevActbegin);
    String previousActivityStartDay = format.asDay(prevActbegin);
    Time realEnd = finishedTrip.endDate();
    String realEndDay = format.asDay(realEnd);
    String realEndTime = format.asTime(realEnd);
    int tripId = finishedTrip.getOid();
    int legId = finishedTrip.getLegId();

    CsvBuilder message = new CsvBuilder();
		message.append(tripId);
		message.append(legId);
    message.append(personnumber);
    message.append(household_oid);
    message.append(personOid);
    message.append(tripBeginDay);
    message.append(activityNumber);
    message.append(tripBeginTime);
    message.append(activityType);
    message.append(previousActivityType);
    message.append(modeType);
		message.append(mainMode);
    message.append(vehicleId);
    message.append(tripEndDay);
    message.append(tripEndTime);
    message.append(distance_km);
    message.append(duration_trip);
    message.append(origin);
    message.append(destination);
    message.append(employmentType);
    message.append(homeZone);
    message.append(activityDuration);
    message.append(previousActivityStartTime);
    message.append(previousActivityStartDay);
    message.append(activityStartTime);
    message.append(activityStartDay);
    message.append(locationParser.serialise(location_from));
    message.append(locationParser.serialise(location_to));
    message.append(sex);
    message.append(tourNumber);
    message.append(isStartOfTour);
    message.append(tourPurpose);
    message.append(isMainActivity);
    message.append(realEndDay);
    message.append(realEndTime);
    return message.toString();
  }

}
