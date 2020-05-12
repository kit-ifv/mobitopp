package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;

public class EmobilityPersonBuilder implements PersonBuilder {

	private static final float defaultEMobilityAcceptance = 0.0f;
	private final PersonBuilder person;
	private float eMobilityAcceptance;
	private PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice;

	public EmobilityPersonBuilder(PersonBuilder person) {
		super();
		this.person = person;
		this.eMobilityAcceptance = defaultEMobilityAcceptance;
		this.chargingInfluencesDestinationChoice = PublicChargingInfluencesDestinationChoice.NEVER;
	}

	@Override
	public PatternActivityWeek getPatternActivityWeek() {
		return person.getPatternActivityWeek();
	}

	@Override
	public EmobilityPersonBuilder setPatternActivityWeek(TourBasedActivityPattern activityPattern) {
		person.setPatternActivityWeek(activityPattern);
		return this;
	}

	@Override
	public PersonBuilder clearPatternActivityWeek() {
		person.clearPatternActivityWeek();
		return this;
	}

	@Override
	public PersonBuilder addPatternActivity(ExtendedPatternActivity pattern) {
		person.addPatternActivity(pattern);
		return this;
	}

	@Override
	public EmobilityPersonBuilder addFixedDestination(FixedDestination fixedDestination) {
		person.addFixedDestination(fixedDestination);
		return this;
	}

	@Override
	public PersonBuilder clearFixedDestinations() {
		person.clearFixedDestinations();
		return this;
	}

	@Override
	public HouseholdForSetup household() {
		return person.household();
	}

	@Override
	public Person toPerson(Household household) {
		Person normal = person.toPerson(household);
		return new EmobilityPerson(normal, eMobilityAcceptance, chargingInfluencesDestinationChoice);
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
	public Graduation graduation() {
		return person.graduation();
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
	public EmobilityPersonBuilder setHasDrivingLicense(boolean hasDrivingLicense) {
		person.setHasDrivingLicense(hasDrivingLicense);
		return this;
	}

	@Override
	public boolean hasCommuterTicket() {
		return person.hasCommuterTicket();
	}

	@Override
	public EmobilityPersonBuilder setHasCommuterTicket(boolean hasCommuterTicket) {
		person.setHasCommuterTicket(hasCommuterTicket);
		return this;
	}

	@Override
	public boolean hasBike() {
		return person.hasBike();
	}

	@Override
	public EmobilityPersonBuilder setHasBike(boolean hasBike) {
		person.setHasBike(hasBike);
		return this;
	}

	@Override
	public boolean hasAccessToCar() {
		return person.hasAccessToCar();
	}

	@Override
	public EmobilityPersonBuilder setHasAccessToCar(boolean hasAccessToCar) {
		person.setHasAccessToCar(hasAccessToCar);
		return this;
	}

	@Override
	public boolean hasPersonalCar() {
		return person.hasPersonalCar();
	}

	@Override
	public EmobilityPersonBuilder setHasPersonalCar(boolean hasPersonalCar) {
		person.setHasPersonalCar(hasPersonalCar);
		return this;
	}

	@Override
	public Zone fixedZoneFor(ActivityType activityType) {
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
	public Stream<FixedDestination> fixedDestinations() {
		return person.fixedDestinations();
	}

	@Override
	public Optional<FixedDestination> getFixedDestination(ActivityType activityType) {
		return person.getFixedDestination(activityType);
	}

	@Override
	public TourBasedActivityPattern getActivityPattern() {
		return person.getActivityPattern();
	}

	@Override
	public boolean hasActivityOfType(ActivityType... activityType) {
		return person.hasActivityOfType(activityType);
	}

	@Override
	public boolean hasActivityOfTypes(Collection<ActivityType> activityTypes) {
		return person.hasActivityOfTypes(activityTypes);
	}

	@Override
	public ModeChoicePreferences getModeChoicePreferences() {
		return person.getModeChoicePreferences();
	}

	@Override
	public ModeChoicePreferences getModeChoicePrefsSurvey() {
		return person.getModeChoicePrefsSurvey();
	}

	@Override
	public ModeChoicePreferences travelTimeSensitivity() {
		return person.travelTimeSensitivity();
	}

	@Override
	public PersonBuilder setTravelTimeSensitivity(ModeChoicePreferences travelTimeSensitivity) {
		return person.setTravelTimeSensitivity(travelTimeSensitivity);
	}

	@Override
	public EmobilityPersonBuilder setModeChoicePreferences(
			ModeChoicePreferences modeChoicePreferences) {
		person.setModeChoicePreferences(modeChoicePreferences);
		return this;
	}

	public float getEmobilityAcceptance() {
		return this.eMobilityAcceptance;
	}

	public EmobilityPersonBuilder setEmobilityAcceptance(float eMobilityAcceptance) {
		this.eMobilityAcceptance = eMobilityAcceptance;
		return this;
	}

	public PublicChargingInfluencesDestinationChoice getChargingInfluencesDestinationChoice() {
		return this.chargingInfluencesDestinationChoice;
	}

	public EmobilityPersonBuilder setChargingInfluencesDestinationChoice(
			PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice) {
		this.chargingInfluencesDestinationChoice = chargingInfluencesDestinationChoice;
		return this;
	}

	@Override
	public Map<String, Boolean> getMobilityProviderMembership() {
		return person.getMobilityProviderMembership();
	}

	@Override
	public PersonBuilder setMobilityProviderMembership(Map<String, Boolean> membership) {
		person.setMobilityProviderMembership(membership);
		return this;
	}

	@Override
	public String toString() {
		return person.toString();
	}

}
