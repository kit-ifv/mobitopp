package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;

public interface PersonBuilder {

  /**
   * Generates a {@link Person} for the {@link Household} based on the configured attributes.
   * 
   * @param household household of the person
   * @return person belonging to the given household with all attributes set
   */
  Person toPerson(Household household);

  PersonId getId();

  HouseholdForSetup household();

  Zone homeZone();

  int age();

  Employment employment();

  Gender gender();

  boolean isMale();

  boolean isFemale();

  Graduation graduation();

  int getIncome();

  Optional<Zone> fixedZoneFor(ActivityType activityType);

  boolean hasFixedZoneFor(ActivityType activityType);

  Zone fixedActivityZone();

  boolean hasFixedActivityZone();

  PersonBuilder setFixedDestination(FixedDestination fixedDestination);

  ModeChoicePreferences getModeChoicePrefsSurvey();

  boolean hasBike();

  PersonBuilder setHasBike(boolean hasBike);

  boolean hasAccessToCar();

  PersonBuilder setHasAccessToCar(boolean hasAccessToCar);

  boolean hasPersonalCar();

  PersonBuilder setHasPersonalCar(boolean hasPersonalCar);

  boolean hasCommuterTicket();

  PersonBuilder setHasCommuterTicket(boolean hasCommuterTicket);

  boolean hasDrivingLicense();

  PersonBuilder setHasDrivingLicense(boolean hasDrivingLicense);

  ModeChoicePreferences getModeChoicePreferences();

  PersonBuilder setModeChoicePreferences(ModeChoicePreferences modeChoicePrefsSurvey);

  TourBasedActivityPattern getActivityPattern();

  PersonBuilder setPatternActivityWeek(TourBasedActivityPattern activityPattern);

  PatternActivityWeek getPatternActivityWeek();

  PersonBuilder setCarsharingMembership(Map<String, Boolean> membership);

}
