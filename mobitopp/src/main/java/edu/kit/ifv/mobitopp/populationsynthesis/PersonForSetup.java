package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface PersonForSetup {

  PatternActivityWeek getPatternActivityWeek();

  void setPatternActivityWeek(TourBasedActivityPattern activityPattern);

  void setFixedDestination(FixedDestination fixedDestination);

  HouseholdForSetup household();

  Person toPerson(Household household);

  PersonId getId();

  boolean isMale();

  boolean isFemale();

  Zone homeZone();

  int getIncome();

  int age();

  Employment employment();

  Gender gender();

  boolean hasDrivingLicense();

  boolean hasCommuterTicket();

  boolean hasBike();

  boolean hasAccessToCar();

  boolean hasPersonalCar();

  Optional<Zone> fixedZoneFor(ActivityType activityType);

  boolean hasFixedZoneFor(ActivityType activityType);

  Zone fixedActivityZone();

  boolean hasFixedActivityZone();

  TourBasedActivityPattern getActivityPattern();
}
