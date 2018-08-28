package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;

public class DefaultPersonFormat implements ForeignKeySerialiserFormat<Person> {

	private static final int personOidIndex = 0;
	private static final int personNumberIndex = 1;
	private static final int householdOidIndex = 2;
	private static final int ageIndex = 3;
	private static final int employmentIndex = 4;
	private static final int genderIndex = 5;
	private static final int incomeIndex = 6;
	private static final int hasBikeIndex = 7;
	private static final int hasAccessIndex = 8;
	private static final int hasPersonalCarIndex = 9;
	private static final int hasCommuterTicketIndex = 10;
	private static final int hasLicenseIndex = 11;
	private static final int preferenceIndex = 12;
	private static final int normalLength = 14;

	public DefaultPersonFormat() {
		super();
	}
	
	@Override
	public List<String> header() {
		return asList("personId", "personNumber", "householdId", "age", "employment", "gender",
				"income", "hasBike", "hasAccessToCar", "hasPersonalCar", "hasCommuterTicket", "hasLicense",
				"preferencesSurvey", "preferencesSimulation", "eMobilityAcceptance",
				"chargingInfluencesDestinationChoice", "carSharingCustomership");
	}

	@Override
	public List<String> prepare(Person person, PopulationContext context) {
			List<String> normalPerson = preparePersonForDemand(person);
		if (person instanceof EmobilityPerson) {
			return prepareEmobilityPerson((EmobilityPerson) person, normalPerson);
		}
		return normalPerson;
	}

	private List<String> prepareEmobilityPerson(EmobilityPerson person, List<String> normalPerson) {
		List<String> emobilityPerson = new ArrayList<>(normalPerson);
		emobilityPerson.add(valueOf(person.eMobilityAcceptance()));
		emobilityPerson.add(valueOf(person.chargingInfluencesDestinantionChoice()));
		emobilityPerson.add(person.carSharingCustomership().toString());
		return emobilityPerson;
	}

	private List<String> preparePersonForDemand(Person person) {
		int personOid = person.attributes().oid;
		PersonId id = person.attributes().id;
		Household household = person.attributes().household;
		int age = person.attributes().age;
		Employment employment = person.attributes().employment;
		Gender gender = person.attributes().gender;
		int income = person.attributes().income;
		boolean hasBike = person.attributes().hasBike;
		boolean hasAccessToCar = person.attributes().hasAccessToCar;
		boolean hasPersonalCar = person.attributes().hasPersonalCar;
		boolean hasCommuterTicket = person.attributes().hasCommuterTicket;
		boolean hasLicense = person.attributes().hasLicense;
		ModeChoicePreferences prefsSurvey = person.modeChoicePrefsSurvey();
		ModeChoicePreferences prefsSimulation = person.modeChoicePreferences();
		return asList( 
				valueOf(personOid), 
				valueOf(id.getPersonNumber()),
				valueOf(household.getOid()), 
				valueOf(age),
				valueOf(employment), 
				valueOf(gender), 
				valueOf(income),
				valueOf(hasBike), 
				valueOf(hasAccessToCar),
				valueOf(hasPersonalCar),
				valueOf(hasCommuterTicket),
				valueOf(hasLicense),
				prefsSurvey.asCommaSeparatedString(),
				prefsSimulation.asCommaSeparatedString()
				);
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
		int oid = personOidOf(data);
		PersonId id = personIdOf(data, household);
		int age = ageOf(data);
		Employment employment = employmentOf(data);
		Gender gender = genderOf(data);
		int income = personIncomeOf(data);
		boolean hasBike = hasBikeOf(data);
		boolean hasAccessToCar = hasAccessToCarOf(data);
		boolean hasPersonalCar = hasPersonalCarOf(data);
		boolean hasCommuterTicket = hasCommuterTicketOf(data);
		boolean hasLicense = hasLicense(data);
		TourBasedActivityPattern activitySchedule = context.activityScheduleFor(oid);
    ModeChoicePreferences modeChoicePrefsSurvey = modeChoicePrefsSurveyOf(data);
    ModeChoicePreferences modeChoicePreferences = modeChoicePreferencesOf(data);
		return new PersonForDemand(oid, id, household, age, employment, gender, income, hasBike,
				hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasLicense, activitySchedule, modeChoicePrefsSurvey, modeChoicePreferences);
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
		return Integer.parseInt(data.get(personOidIndex));
	}

	private PersonId personIdOf(List<String> data, Household household) {
		int personNumber = Integer.parseInt(data.get(personNumberIndex));
		return new PersonId(household.getId(), personNumber);
	}

	private Optional<Household> householdOf(List<String> data, PopulationContext context) {
		int householdOid = Integer.parseInt(data.get(householdOidIndex));
		return context.getHouseholdByOid(householdOid);
	}

	private int ageOf(List<String> data) {
		return Integer.parseInt(data.get(ageIndex));
	}

	private Employment employmentOf(List<String> data) {
		return Employment.valueOf(data.get(employmentIndex));
	}

	private Gender genderOf(List<String> data) {
		return Gender.valueOf(data.get(genderIndex));
	}

	private int personIncomeOf(List<String> data) {
		return Integer.parseInt(data.get(incomeIndex));
	}

	private boolean hasBikeOf(List<String> data) {
		return Boolean.parseBoolean(data.get(hasBikeIndex));
	}

	private boolean hasAccessToCarOf(List<String> data) {
		return Boolean.parseBoolean(data.get(hasAccessIndex));
	}

	private boolean hasPersonalCarOf(List<String> data) {
		return Boolean.parseBoolean(data.get(hasPersonalCarIndex));
	}

	private boolean hasCommuterTicketOf(List<String> data) {
		return Boolean.parseBoolean(data.get(hasCommuterTicketIndex));
	}

	private boolean hasLicense(List<String> data) {
		return Boolean.parseBoolean(data.get(hasLicenseIndex));
	}

	private float eMobilityAcceptanceOf(List<String> data) {
		return Float.parseFloat(data.get(normalLength));
	}

	private PublicChargingInfluencesDestinationChoice destinationChoiceInfluenceOf(List<String> data) {
		return PublicChargingInfluencesDestinationChoice.valueOf(data.get(normalLength + 1));
	}

	private Map<String, Boolean> carSharingOf(List<String> data) {
		String carSharings = data.get(normalLength + 2);
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
		String preferences = data.get(preferenceIndex);
		return ModeChoicePreferences.fromCommaSeparatedString(preferences);
	}
	
	private ModeChoicePreferences modeChoicePreferencesOf(List<String> data) {
		assert data.size() > preferenceIndex;
		assert data.size() > preferenceIndex+1 : data;
		String preferences = data.get(preferenceIndex + 1);
		return ModeChoicePreferences.fromCommaSeparatedString(preferences);
	}

	private void add(String entry, TreeMap<String, Boolean> carSharingCompanies) {
		String[] split = entry.split("=");
		String company = split[0].trim();
		Boolean customer = Boolean.parseBoolean(split[1].trim());
		carSharingCompanies.put(company, customer);
	}
}
