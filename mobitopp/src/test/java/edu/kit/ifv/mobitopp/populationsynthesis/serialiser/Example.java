package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPatternCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.FixedDestinations;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;
import edu.kit.ifv.mobitopp.time.Time;

public class Example {

  public static final int type = 7;
  public static final int observedTripDuration = 2;
  public static final Time startTime = Time.start.plusMinutes(3);
  public static final int duration = 4;
  public static final int tourNumber = 0;
  public static final boolean isMainActivity = false;
  public static final boolean isInSupertour = false;

  public static final Location location = new Location(new Point2D.Double(1.0, 2.0), 1, 0.5);
  public static final int age = 3;
  public static final Employment employment = Employment.FULLTIME;
  public static final Gender gender = Gender.MALE;
  public static final boolean hasBike = true;
  public static final boolean hasAccessToCar = true;
  public static final boolean hasPersonalCar = true;
  public static final boolean hasCommuterTicket = true;
  public static final boolean hasLicense = true;
  public static final int income = 1;
  public static final float eMobilityAcceptance = 1.0f;
  public static final PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice = PublicChargingInfluencesDestinationChoice.ALWAYS;

  public static Person personOf(Household household, int personNumber, Zone zone) {
    return personOf(household, personNumber, zone, ActivityType.HOME);
  }

  public static Person personOf(
      Household household, int personNumber, Zone zone, ActivityType activityType) {
    PersonId id = new PersonId(personNumber, household.getId(), personNumber);
    TourBasedActivityPattern activitySchedule = activitySchedule();
    FixedDestinations fixedDestinations = new FixedDestinations();
    fixedDestinations.add(new FixedDestination(activityType, zone, location));
    return new PersonForDemand(id, household, age, employment, gender, income,
        hasBike, hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasLicense, activitySchedule,
        fixedDestinations, ModeChoicePreferences.NOPREFERENCES, ModeChoicePreferences.NOPREFERENCES);
  }

  public static TourBasedActivityPattern activitySchedule() {
    PatternActivityWeek schedule = new PatternActivityWeek();
    schedule.addPatternActivity(activity());
    return TourBasedActivityPatternCreator.fromPatternActivityWeek(schedule);
  }

  public static ExtendedPatternActivity activity() {
    return new ExtendedPatternActivity(0, false, false, ActivityType.getTypeFromInt(type),
        observedTripDuration, startTime, duration);
  }

  public static Person emobilityPersonOf(Household household, int personNumber, Zone zone) {
    Map<String, Boolean> carSharingCustomership = carSharingCustomership();
    return new EmobilityPerson(personOf(household, personNumber, zone, ActivityType.WORK),
        eMobilityAcceptance, chargingInfluencesDestinationChoice, carSharingCustomership);
  }

  public static Map<String, Boolean> carSharingCustomership() {
    Map<String, Boolean> carSharingCustomership = new HashMap<>();
    carSharingCustomership.put("company-one", true);
    carSharingCustomership.put("company-two", false);
    return carSharingCustomership;
  }
}
