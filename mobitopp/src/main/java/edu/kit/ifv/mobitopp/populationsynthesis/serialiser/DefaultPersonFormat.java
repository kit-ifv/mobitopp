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
import edu.kit.ifv.mobitopp.populationsynthesis.FixedDestinations;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;

public class DefaultPersonFormat implements ForeignKeySerialiserFormat<Person> {

	private final int normalLength;
  private final ColumnMapping<Person> defaultColumns;
  private final ColumnMapping<EmobilityPerson> eMobilityColumns;

	public DefaultPersonFormat() {
		super();
    defaultColumns = new ColumnMapping<>();
    defaultColumns.add("personId", Person::getOid);
    defaultColumns.add("personNumber", e -> e.getId().getPersonNumber());
    defaultColumns.add("householdId", e -> e.household().getOid());
    defaultColumns.add("age", Person::age);
    defaultColumns.add("employment", Person::employment);
    defaultColumns.add("gender", Person::gender);
    defaultColumns.add("graduation", e -> e.graduation().getNumeric());
    defaultColumns.add("income", Person::getIncome);
    defaultColumns.add("hasBike", Person::hasBike);
    defaultColumns.add("hasAccessToCar", Person::hasAccessToCar);
    defaultColumns.add("hasPersonalCar", Person::hasPersonalCar);
    defaultColumns.add("hasCommuterTicket", Person::hasCommuterTicket);
    defaultColumns.add("hasLicense", Person::hasDrivingLicense);
    defaultColumns
        .add("preferencesSurvey", e -> e.modeChoicePrefsSurvey().asCommaSeparatedString());
    defaultColumns
        .add("preferencesSimulation", e -> e.modeChoicePreferences().asCommaSeparatedString());
    normalLength = defaultColumns.header().size();
    eMobilityColumns = new ColumnMapping<>(normalLength);
    eMobilityColumns.add("eMobilityAcceptance", EmobilityPerson::eMobilityAcceptance);
    eMobilityColumns
        .add("chargingInfluencesDestinationChoice",
            EmobilityPerson::chargingInfluencesDestinantionChoice);
    eMobilityColumns.add("carSharingCustomership", e -> e.carSharingCustomership().toString());
  }
	
	@Override
	public List<String> header() {
	  ArrayList<String> columns = new ArrayList<>(defaultColumns.header());
	  columns.addAll(eMobilityColumns.header());
    return columns;
	}

	@Override
	public List<String> prepare(Person person) {
			List<String> normalPerson = preparePersonForDemand(person);
		if (person instanceof EmobilityPerson) {
			return prepareEmobilityPerson((EmobilityPerson) person, normalPerson);
		}
		return normalPerson;
	}

	private List<String> prepareEmobilityPerson(EmobilityPerson person, List<String> normalPerson) {
		List<String> emobilityPerson = new ArrayList<>(normalPerson);
		emobilityPerson.addAll(eMobilityColumns.prepare(person));
		return emobilityPerson;
	}

	private List<String> preparePersonForDemand(Person person) {
	  return defaultColumns.prepare(person);
	}

	@Override
	public Optional<Person> parse(List<String> data, PopulationContext context) {
		Optional<Person> personForDemand = personForDemand(data, context);
		Optional<Person> person = personForDemand.map(p -> wrapInEmobility(data, p));
		person.ifPresent(p -> p.household().addPerson(p));
		return person;
	}

	private Optional<Person> personForDemand(List<String> data, PopulationContext context) {
		return householdOf(data, context).map(household -> createPerson(data, context, household));
	}

	private Person createPerson(List<String> data, PopulationContext context, Household household) {
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
    PersonForDemand person = new PersonForDemand(id, household, age, employment, gender,
        graduation, income, hasBike, hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasLicense,
        activitySchedule, fixedDestinations,
        modeChoicePrefsSurvey, modeChoicePreferences);
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

  private Person wrapInEmobility(List<String> data, Person person) {
		if (normalLength == data.size()) {
			return person;
		}
		float eMobilityAcceptance = eMobilityAcceptanceOf(data);
		PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice = destinationChoiceInfluenceOf(
				data);
		Map<String, Boolean> carSharingCustomership = carSharingOf(data);
		return new EmobilityPerson(person, eMobilityAcceptance, chargingInfluencesDestinationChoice,
				carSharingCustomership);
	}

	private int personOidOf(List<String> data) {
		return defaultColumns.get("personId", data).asInt();
	}

	private PersonId personIdOf(List<String> data, Household household) {
	  int oid = personOidOf(data);
		int personNumber = defaultColumns.get("personNumber", data).asInt();
		return new PersonId(oid, household.getId(), personNumber);
	}

	private Optional<Household> householdOf(List<String> data, PopulationContext context) {
		int householdOid = defaultColumns.get("householdId", data).asInt();
		return context.getHouseholdByOid(householdOid);
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
