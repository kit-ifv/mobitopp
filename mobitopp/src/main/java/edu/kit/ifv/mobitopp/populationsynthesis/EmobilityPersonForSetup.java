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
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;

public class EmobilityPersonForSetup implements PersonForSetup {

  private final PersonForSetup person;
  private float eMobilityAcceptance;
  private PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice;
  private Map<String, Boolean> carSharingCustomership;

  public EmobilityPersonForSetup(
      PersonForSetup person, float eMobilityAcceptance,
      PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice,
      Map<String, Boolean> carSharingCustomership) {
    super();
    this.person = person;
    this.eMobilityAcceptance = eMobilityAcceptance;
    this.chargingInfluencesDestinationChoice = chargingInfluencesDestinationChoice;
    this.carSharingCustomership = carSharingCustomership;
  }

  @Override
  public PatternActivityWeek getPatternActivityWeek() {
    return person.getPatternActivityWeek();
  }

  @Override
  public void setPatternActivityWeek(TourBasedActivityPattern activityPattern) {
    person.setPatternActivityWeek(activityPattern);
  }

  @Override
  public void setFixedDestination(FixedDestination fixedDestination) {
    person.setFixedDestination(fixedDestination);
  }

  @Override
  public HouseholdForSetup household() {
    return person.household();
  }

  @Override
  public Person toPerson(Household household) {
    Person normal = person.toPerson(household);
    return new EmobilityPerson(normal, eMobilityAcceptance, chargingInfluencesDestinationChoice,
        carSharingCustomership);
  }

  @Override
  public boolean isMale() {
    return person.isMale();
  }

  @Override
  public boolean isFemale() {
    return person.isFemale();
  }

  @Override
  public Zone homeZone() {
    return person.homeZone();
  }

  @Override
  public int getIncome() {
    return person.getIncome();
  }

  @Override
  public int age() {
    return person.age();
  }

  @Override
  public Employment employment() {
    return person.employment();
  }

  @Override
  public Gender gender() {
    return person.gender();
  }

  @Override
  public PersonId getId() {
    return person.getId();
  }

  @Override
  public boolean hasDrivingLicense() {
    return person.hasDrivingLicense();
  }

  @Override
  public boolean hasCommuterTicket() {
    return person.hasCommuterTicket();
  }
  
  @Override
  public void setCommuterTicket(boolean hasCommuterTicket) {
    person.setCommuterTicket(hasCommuterTicket);
  }

  @Override
  public boolean hasBike() {
    return person.hasBike();
  }

  @Override
  public boolean hasAccessToCar() {
    return person.hasAccessToCar();
  }

  @Override
  public boolean hasPersonalCar() {
    return person.hasPersonalCar();
  }
  
  @Override
  public Optional<Zone> fixedZoneFor(ActivityType activityType) {
    return person.fixedZoneFor(activityType);
  }

  @Override
  public boolean hasFixedZoneFor(ActivityType activityType) {
    return person.hasFixedZoneFor(activityType);
  }

  @Override
  public Zone fixedActivityZone() {
    return person.fixedActivityZone();
  }

  @Override
  public boolean hasFixedActivityZone() {
    return person.hasFixedActivityZone();
  }

  @Override
  public TourBasedActivityPattern getActivityPattern() {
    return person.getActivityPattern();
  }

}
