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
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;

public class DefaultPersonForSetup implements PersonForSetup {

  private final PersonId id;
  private final HouseholdForSetup household;
  private final short age;
  private final Employment employment;
  private final Gender gender;
  private final Graduation graduation;
  private final int income;
  private final boolean hasBike;
  private final boolean hasAccessToCar;
  private final boolean hasPersonalCar;
  private boolean hasCommuterTicket;
  private final boolean hasDrivingLicense;
  private final FixedDestinations fixedDestinations;
  private final ModeChoicePreferences modeChoicePrefsSurvey;
  private final ModeChoicePreferences modeChoicePreferences;
  private TourBasedActivityPattern activityPattern;

  public DefaultPersonForSetup(
      PersonId id, HouseholdForSetup household, int age, Employment employment, Gender gender,
      Graduation graduation, int income, boolean hasBike, boolean hasAccessToCar,
      boolean hasPersonalCar, boolean hasCommuterTicket, boolean hasLicense,
      ModeChoicePreferences modeChoicePrefsSurvey, ModeChoicePreferences modeChoicePreferences) {
    this.id = id;

    this.household = household;

    this.age = (short) age;

    this.employment = employment;
    this.gender = gender;
    this.graduation = graduation;

    this.income = income;

    this.hasBike = hasBike;
    this.hasAccessToCar = hasAccessToCar;
    this.hasPersonalCar = hasPersonalCar;
    this.hasCommuterTicket = hasCommuterTicket;
    this.hasDrivingLicense = hasLicense;

    fixedDestinations = new FixedDestinations();

    this.modeChoicePrefsSurvey = modeChoicePrefsSurvey;
    this.modeChoicePreferences = modeChoicePreferences;

  }

  @Override
  public HouseholdForSetup household() {
    return household;
  }

  @Override
  public boolean hasPersonalCar() {
    return hasPersonalCar;
  }

  @Override
  public boolean hasAccessToCar() {
    return hasAccessToCar;
  }

  @Override
  public boolean hasBike() {
    return hasBike;
  }

  @Override
  public boolean hasCommuterTicket() {
    return hasCommuterTicket;
  }
  
  @Override
  public void setCommuterTicket(boolean hasCommuterTicket) {
    this.hasCommuterTicket = hasCommuterTicket;
  }

  @Override
  public boolean hasDrivingLicense() {
    return hasDrivingLicense;
  }

  @Override
  public PersonId getId() {
    return id;
  }

  @Override
  public Gender gender() {
    return gender;
  }

  @Override
  public Employment employment() {
    return employment;
  }
  
  @Override
  public Graduation graduation() {
    return graduation;
  }

  @Override
  public int age() {
    return age;
  }

  @Override
  public int getIncome() {
    return income;
  }

  @Override
  public Zone homeZone() {
    return household.homeZone();
  }

  @Override
  public boolean isFemale() {
    return Gender.FEMALE.equals(gender);
  }

  @Override
  public boolean isMale() {
    return Gender.MALE.equals(gender);
  }

  @Override
  public PatternActivityWeek getPatternActivityWeek() {
    return new PatternActivityWeek(activityPattern.asPatternActivities());
  }

  @Override
  public void setPatternActivityWeek(TourBasedActivityPattern activityPattern) {
    this.activityPattern = activityPattern;
  }

  @Override
  public TourBasedActivityPattern getActivityPattern() {
    return activityPattern;
  }

  @Override
  public void setFixedDestination(FixedDestination fixedDestination) {
    fixedDestinations.add(fixedDestination);
  }

  @Override
  public boolean hasFixedZoneFor(ActivityType activityType) {
    return fixedDestinations.hasDestination(activityType);
  }

  @Override
  public Optional<Zone> fixedZoneFor(ActivityType activityType) {
    return fixedDestinations.getDestination(activityType).map(FixedDestination::zone);
  }

  @Override
  public boolean hasFixedActivityZone() {
    return this.fixedDestinations.hasFixedDestination();
  }

  @Override
  public Zone fixedActivityZone() {
    return fixedDestinations
        .getFixedDestination()
        .map(FixedDestination::zone)
        .orElseGet(() -> household().homeZone());
  }

  @Override
  public Person toPerson(Household household) {
    return new PersonForDemand(id, household, age, employment, gender, graduation, income, hasBike,
        hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasDrivingLicense, activityPattern,
        fixedDestinations, modeChoicePrefsSurvey, modeChoicePreferences);
  }
  
  @Override
  public String toString() {
    return id.toString();
  }
}
