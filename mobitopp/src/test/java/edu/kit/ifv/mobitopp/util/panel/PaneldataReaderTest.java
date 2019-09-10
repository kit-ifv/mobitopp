package edu.kit.ifv.mobitopp.util.panel;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.data.local.ExamplePersonOfPanelData;
import edu.kit.ifv.mobitopp.result.CsvBuilder;

public class PaneldataReaderTest {

	private static final PersonOfPanelData person = ExamplePersonOfPanelData.aPerson;
	private static final HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
	private static final String id = valueOf(household.id().getHouseholdNumber());
	private static final String year = valueOf(household.id().getYear());
	private static final String areaType = valueOf(household.areaTypeAsInt());
	private static final String size = valueOf(household.size());
	private static final String cars = valueOf(household.getNumberOfCars());
	private static final String personNumber = valueOf(person.getId().getPersonNumber());
	private static final String sex = valueOf(person.getGenderTypeAsInt());
	private static final String birthYear = valueOf(person.getBirthyear());
	private static final String employmentType = valueOf(person.employment().getTypeAsInt());
	private static final String commuterTicket = person.hasCommuterTicket() ? "1" : "0";
	private static final String hhIncome = valueOf(household.income());
	private static final String hhIncomeClass = valueOf(household.incomeClass());
	private static final String bicycle = person.hasBicycle() ? "1" : "0";
	private static final String patternValue = "-1;7;460;-1;15;1;275;475;12;7;278;-1";
	private static final String patternKey = "activitypattern";
	private static final String hhType = valueOf(household.domCode());
	private static final String notContainedChildren = valueOf(
			household.getNumberOfNotReportingChildren());
	private static final String notContainedChildrenMaxAge = "10";
	private static final String poleDistance = valueOf(person.getPoleDistance());
	private static final String carAvailable = person.hasAccessToCar() ? "1" : "0";
	private static final String personalCar = person.hasPersonalCar() ? "1" : "0";
	private static final String graduation = valueOf(person.getGraduation());
	private static final String hhActivityRadiusTime = valueOf(household.getActivityRadius());
	private static final String hhActivityRadiusMode = "1";
	private static final String distanceWork = valueOf(person.getDistanceWork());
	private static final String distanceEducation = valueOf(person.getDistanceEducation());
	private static final String licence = "1";
	private LinkedHashMap<String, String> example;

	@BeforeEach
	public void initialise() {
		example = new LinkedHashMap<>();
	}

	private String createExample() {
		CsvBuilder exampleBuilder = new CsvBuilder(";");
		example.keySet().forEach(exampleBuilder::append);
		exampleBuilder.newLine(patternKey);
		example.values().forEach(exampleBuilder::append);
		exampleBuilder.newLine(patternValue);
		return exampleBuilder.toString();
	}

	private void addOldColumnNames() {
		LinkedHashMap<String, String> optional = new LinkedHashMap<>();
		optional.put("caravailable", carAvailable);
		optional.put("personalcar", personalCar);
		optional.forEach(example::put);
	}

	private void addNewColumnNames() {
		LinkedHashMap<String, String> required = new LinkedHashMap<>();
		required.put("licence", licence);
		required.forEach(example::put);
	}

	private void addOptional() {
		LinkedHashMap<String, String> optional = new LinkedHashMap<>();
		optional.put("hhtype", hhType);
		optional.put("notcontainedchildren", notContainedChildren);
		optional.put("notcontainedchildrenmaxage", notContainedChildrenMaxAge);
		optional.put("poledistance", poleDistance);
		optional.put("caravailable", carAvailable);
		optional.put("personalcar", personalCar);
		optional.put("graduation", graduation);
		optional.put("hhincome_class", hhIncomeClass);
		optional.put("hhactivity_radius_time", hhActivityRadiusTime);
		optional.put("hhactivity_radius_mode", hhActivityRadiusMode);
		optional.put("distance_work", distanceWork);
		optional.put("distance_education", distanceEducation);
		optional.forEach(example::put);
	}

	private void addRequired() {
		LinkedHashMap<String, String> required = new LinkedHashMap<>();
		required.put("#ID", id);
		required.put("year", year);
		required.put("areatype", areaType);
		required.put("size", size);
		required.put("cars", cars);
		required.put("personnumber", personNumber);
		required.put("sex", sex);
		required.put("birthyear", birthYear);
		required.put("employmenttype", employmentType);
		required.put("commuterticket", commuterTicket);
		required.put("hhincome", hhIncome);
		required.put("bicycle", bicycle);
		required.forEach(example::put);
	}

	@Test
	void reportsMissingColumnsWithOtherName() throws Exception {
		addRequired();
		addOldColumnNames();
		final LinkedHashMap<String, String> missingColumns = new LinkedHashMap<>();
		missingColumns.put("gender", "sex");
		missingColumns.put("licence", "caravailable and personalcar");
		missingColumns.put("hhincome_class", "0");
		missingColumns.put("incomeperson", "0");
		missingColumns.put("pref_cardriver", "0.0");
		missingColumns.put("pref_walking", "0.0");
		missingColumns.put("pref_cycling", "0.0");
		missingColumns.put("pref_publictransport", "0.0");

		final PaneldataReader reader = createReader();

		assertAll(missingColumns
				.entrySet()
				.stream()
				.map(e -> () -> assertThat(reader.missingColumns()).contains(e)));
	}

	@Test
	void reportsMissingOptionalColumns() throws Exception {
		addRequired();
		addNewColumnNames();
		final LinkedHashMap<String, String> missingColumns = new LinkedHashMap<>();
		missingColumns.put("hhactivity_radius_time", "0.0");
		missingColumns.put("hhactivity_radius_mode", "IV");
		missingColumns.put("distance_work", "0.0");
		missingColumns.put("distance_education", "0.0");

		final PaneldataReader reader = createReader();

		assertAll(missingColumns
				.entrySet()
				.stream()
				.map(e -> () -> assertThat(reader.missingColumns()).contains(e)));
	}

	@Test
	void reportsMissingColumnsWhenOptionalOnesArePresent() throws Exception {
		addRequired();
		addNewColumnNames();
		addOptional();
		final List<String> optionalColumns = new LinkedList<>();
		optionalColumns.add("hhactivity_radius_time");
		optionalColumns.add("hhactivity_radius_mode");
		optionalColumns.add("distance_work");
		optionalColumns.add("distance_education");

		final PaneldataReader reader = createReader();

		assertAll(optionalColumns
				.stream()
				.map(e -> () -> assertThat(reader.missingColumns()).doesNotContainKey(e)));
	}

	@Test
	void parsesRequiredHouseholdAttributes() throws Exception {
		addRequired();
		addOptional();
		List<HouseholdOfPanelData> households = createReader().readHouseholds();

		assertThat(households).contains(household);
	}
	
	@Test
	void parsesRequiredPersonAttributes() throws Exception {
		addRequired();
		addOptional();
		List<PersonOfPanelData> persons = createReader().readPersons();
		
		assertThat(persons).contains(person);
	}

	private PaneldataReader createReader() {
		final String input = createExample();
		final Reader reader = new InputStreamReader(new ByteArrayInputStream(input.getBytes()));
		final File file = new File("some");
		return new PaneldataReader(file) {

			@Override
			Reader readerFor(File file) throws IOException {
				return reader;
			}
		};
	}
}
