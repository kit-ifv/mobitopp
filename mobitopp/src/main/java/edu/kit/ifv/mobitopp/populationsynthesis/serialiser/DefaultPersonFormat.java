package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.populationsynthesis.ColumnMapping;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPersonForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.EmobilityPersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.FixedDestinations;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;

public class DefaultPersonFormat implements ForeignKeySerialiserFormat<PersonBuilder> {

	private final int normalLength;
  private final ColumnMapping<PersonBuilder> defaultColumns;
  private final ColumnMapping<EmobilityPersonBuilder> eMobilityColumns;

	public DefaultPersonFormat() {
		super();
    defaultColumns = new ColumnMapping<>();
    defaultColumns.add("personId", e -> e.getId().getOid());
    defaultColumns.add("personNumber", e -> e.getId().getPersonNumber());
    defaultColumns.add("householdId", e -> e.household().getId().getOid());
    defaultColumns.add("age", PersonBuilder::age);
    defaultColumns.add("employment", PersonBuilder::employment);
    defaultColumns.add("gender", PersonBuilder::gender);
    defaultColumns.add("graduation", e -> e.graduation().getNumeric());
    defaultColumns.add("income", PersonBuilder::getIncome);
    defaultColumns.add("hasBike", PersonBuilder::hasBike);
    defaultColumns.add("hasAccessToCar", PersonBuilder::hasAccessToCar);
    defaultColumns.add("hasPersonalCar", PersonBuilder::hasPersonalCar);
    defaultColumns.add("hasCommuterTicket", PersonBuilder::hasCommuterTicket);
    defaultColumns.add("hasLicense", PersonBuilder::hasDrivingLicense);
    defaultColumns
        .add("preferencesSurvey", e -> e.getModeChoicePrefsSurvey().asCommaSeparatedString());
    defaultColumns
        .add("preferencesSimulation", e -> e.getModeChoicePreferences().asCommaSeparatedString());
    normalLength = defaultColumns.header().size();
    eMobilityColumns = new ColumnMapping<>(normalLength);
    eMobilityColumns.add("eMobilityAcceptance", EmobilityPersonBuilder::getEmobilityAcceptance);
    eMobilityColumns
        .add("chargingInfluencesDestinationChoice",
            EmobilityPersonBuilder::getChargingInfluencesDestinationChoice);
    eMobilityColumns.add("carSharingCustomership", e -> e.getCarsharingMembership().toString());
  }
	
	@Override
	public List<String> header() {
	  ArrayList<String> columns = new ArrayList<>(defaultColumns.header());
	  columns.addAll(eMobilityColumns.header());
    return columns;
	}

	@Override
	public List<String> prepare(PersonBuilder person) {
			List<String> normalPerson = preparePersonForDemand(person);
		if (person instanceof EmobilityPersonBuilder) {
			return prepareEmobilityPerson((EmobilityPersonBuilder) person, normalPerson);
		}
		return normalPerson;
	}

	private List<String> prepareEmobilityPerson(EmobilityPersonBuilder person, List<String> normalPerson) {
		List<String> emobilityPerson = new ArrayList<>(normalPerson);
		emobilityPerson.addAll(eMobilityColumns.prepare(person));
		return emobilityPerson;
	}

	private List<String> preparePersonForDemand(PersonBuilder person) {
	  return defaultColumns.prepare(person);
	}

	@Override
	public Optional<PersonBuilder> parse(List<String> data, PopulationContext context) {
		Optional<PersonBuilder> personForDemand = personForDemand(data, context);
		Optional<PersonBuilder> person = personForDemand.map(p -> wrapInEmobility(data, p));
		person.ifPresent(p -> p.household().addPerson(p));
		return person;
	}

	private Optional<PersonBuilder> personForDemand(List<String> data, PopulationContext context) {
		return householdOf(data, context).map(household -> createPerson(data, context, household));
	}

	private PersonBuilder createPerson(
			List<String> data, PopulationContext context, HouseholdForSetup household) {
		PersonId id = personIdOf(data, household);
		int age = ageOf(data);
		Employment employment = employmentOf(data);
		Gender gender = genderOf(data);
		Graduation graduation = graduationOf(data);
		int income = personIncomeOf(data);
		boolean hasBike = hasBikeOf(data);
		boolean hasAccessToCar = hasAccessToCarOf(data);
		boolean hasPersonalCar = hasPersonalCarOf(data);
		boolean hasCommuterTicket = hasCommuterTicketOf(data);
		boolean hasLicense = hasLicense(data);
		TourBasedActivityPattern activitySchedule = context.activityScheduleFor(id.getOid());
    ModeChoicePreferences modeChoicePrefsSurvey = modeChoicePrefsSurveyOf(data);
    ModeChoicePreferences modeChoicePreferences = modeChoicePreferencesOf(data);
    FixedDestinations fixedDestinations = fixedDestinations(id, context);
		DefaultPersonForSetup person = new DefaultPersonForSetup(id, household, age, employment,
				gender, graduation, income, modeChoicePrefsSurvey);
		person.setHasBike(hasBike);
    person.setHasAccessToCar(hasAccessToCar);
    person.setHasPersonalCar(hasPersonalCar);
    person.setHasCommuterTicket(hasCommuterTicket);
    person.setHasDrivingLicense(hasLicense);
    person.setPatternActivityWeek(activitySchedule);
    person.setModeChoicePreferences(modeChoicePreferences);
    fixedDestinations.stream().forEach(person::setFixedDestination);
		return person;
	}

	private Graduation graduationOf(List<String> data) {
	  return Graduation.getTypeFromNumeric(defaultColumns.get("graduation", data).asInt());
  }

  private FixedDestinations fixedDestinations(PersonId id, PopulationContext context) {
	  FixedDestinations fixedDestinations = new FixedDestinations();
	  context.destinations(id).forEach(fixedDestinations::add);
    return fixedDestinations;
  }

	private PersonBuilder wrapInEmobility(List<String> data, PersonBuilder person) {
		if (normalLength == data.size()) {
			return person;
		}
		EmobilityPersonBuilder emobilityPerson = new EmobilityPersonBuilder(person);
		float eMobilityAcceptance = eMobilityAcceptanceOf(data);
		PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice = destinationChoiceInfluenceOf(
				data);
		Map<String, Boolean> carSharingCustomership = carSharingOf(data);
		emobilityPerson.setEmobilityAcceptance(eMobilityAcceptance);
		emobilityPerson.setChargingInfluencesDestinationChoice(chargingInfluencesDestinationChoice);
		emobilityPerson.setCarsharingMembership(carSharingCustomership);
		return emobilityPerson;
	}

	private int personOidOf(List<String> data) {
		return defaultColumns.get("personId", data).asInt();
	}

	private PersonId personIdOf(List<String> data, HouseholdForSetup household) {
	  int oid = personOidOf(data);
		int personNumber = defaultColumns.get("personNumber", data).asInt();
		return new PersonId(oid, household.getId(), personNumber);
	}

	private Optional<HouseholdForSetup> householdOf(List<String> data, PopulationContext context) {
		int householdOid = defaultColumns.get("householdId", data).asInt();
		return context.getHouseholdForSetupByOid(householdOid);
	}

	private int ageOf(List<String> data) {
		return defaultColumns.get("age", data).asInt();
	}

	private Employment employmentOf(List<String> data) {
		return Employment.valueOf(defaultColumns.get("employment", data).asString());
	}

	private Gender genderOf(List<String> data) {
		return Gender.valueOf(defaultColumns.get("gender", data).asString());
	}

	private int personIncomeOf(List<String> data) {
		return defaultColumns.get("income", data).asInt();
	}

	private boolean hasBikeOf(List<String> data) {
		return defaultColumns.get("hasBike", data).asBoolean();
	}

	private boolean hasAccessToCarOf(List<String> data) {
	  return defaultColumns.get("hasAccessToCar", data).asBoolean();
	}

	private boolean hasPersonalCarOf(List<String> data) {
	  return defaultColumns.get("hasPersonalCar", data).asBoolean();
	}

	private boolean hasCommuterTicketOf(List<String> data) {
	  return defaultColumns.get("hasCommuterTicket", data).asBoolean();
	}

	private boolean hasLicense(List<String> data) {
	  return defaultColumns.get("hasLicense", data).asBoolean();
	}

	private float eMobilityAcceptanceOf(List<String> data) {
		return eMobilityColumns.get("eMobilityAcceptance", data).asFloat();
	}

	private PublicChargingInfluencesDestinationChoice destinationChoiceInfluenceOf(List<String> data) {
    return PublicChargingInfluencesDestinationChoice
        .valueOf(eMobilityColumns.get("chargingInfluencesDestinationChoice", data).asString());
	}

	private Map<String, Boolean> carSharingOf(List<String> data) {
		String carSharings = eMobilityColumns.get("carSharingCustomership", data).asString();
		String pureEntries = carSharings.substring(1, carSharings.length() - 1);
		return parseCarSharing(pureEntries);
	}

	private Map<String, Boolean> parseCarSharing(String pureEntries) {
		TreeMap<String, Boolean> toCarSharingCompanies = new TreeMap<>();
		StringTokenizer entries = new StringTokenizer(pureEntries, ",");
		while(entries.hasMoreTokens()) {
			String company = entries.nextToken();
			add(company, toCarSharingCompanies);
		}
		return toCarSharingCompanies;
	}

	private ModeChoicePreferences modeChoicePrefsSurveyOf(List<String> data) {
		String preferences = defaultColumns.get("preferencesSurvey", data).asString();
		return ModeChoicePreferences.fromCommaSeparatedString(preferences);
	}
	
	private ModeChoicePreferences modeChoicePreferencesOf(List<String> data) {
		String preferences = defaultColumns.get("preferencesSimulation", data).asString();
		return ModeChoicePreferences.fromCommaSeparatedString(preferences);
	}

	private void add(String entry, TreeMap<String, Boolean> carSharingCompanies) {
		String[] split = entry.split("=");
		String company = split[0].trim();
		Boolean customer = Boolean.parseBoolean(split[1].trim());
		carSharingCompanies.put(company, customer);
	}
}
