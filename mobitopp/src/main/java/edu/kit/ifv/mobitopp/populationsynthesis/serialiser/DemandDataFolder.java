package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput.activity;
import static edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput.car;
import static edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput.fixedDestination;
import static edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput.household;
import static edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput.opportunity;
import static edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput.person;

import java.io.File;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultFixedDestinationFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPrivateCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class DemandDataFolder {

	private final File demandDataFolder;
	private final InputFormats formats;
	private final PersonChanger personChanger;

	private DemandDataFolder(
			final File demandDataFolder, final InputFormats formats, final PersonChanger personChanger) {
		super();
		this.demandDataFolder = demandDataFolder;
		this.formats = formats;
		this.personChanger = personChanger;
	}

	public static DemandDataFolder at(
			final File demandDataFolder, final ZoneRepository zoneRepository,
			final ZoneRepository zonesToSimulate, final PersonChanger personChanger)
			throws IOException {
		initialiseDirectory(demandDataFolder);
		InputFormats formats = new InputFormats(zoneRepository, zonesToSimulate);
		return new DemandDataFolder(demandDataFolder, formats, personChanger);
	}

	private static void initialiseDirectory(File folder) {
		if (folder.isFile()) {
			throw new IllegalArgumentException(
					"Path does not point to a directory: " + folder.getAbsolutePath());
		}
		folder.mkdirs();
	}

	private Serialiser<HouseholdForSetup> households() throws IOException {
		DefaultHouseholdFormat householdFormat = householdFormat();
		return serialiser(household, householdFormat);
	}

	private ForeignKeySerialiser<PersonBuilder> persons(PersonChanger personChanger)
			throws IOException {
		DefaultPersonFormat format = personFormat(personChanger);
		return serialiser(person, format);
	}

	private Serialiser<PersonPatternActivity> activities() throws IOException {
		DefaultActivityFormat format = activityFormat();
		return serialiser(activity, format);
	}

	private ForeignKeySerialiser<PrivateCarForSetup> cars() throws IOException {
		DefaultPrivateCarFormat format = privateCarFormat();
		return serialiser(car, format);
	}

	private Serialiser<PersonFixedDestination> fixedDestinations() throws IOException {
		DefaultFixedDestinationFormat format = fixedDestinationFormat();
		return serialiser(fixedDestination, format);
	}

	private Serialiser<Opportunity> opportunitys() throws IOException {
		DefaultOpportunityFormat format = opportunityFormat();
		return serialiser(opportunity, format);
	}

	public DemandDataSerialiser serialiseAsCsv() throws IOException {
		DefaultDemandDataSerialiser serialiser = new DefaultDemandDataSerialiser(households(),
				persons(PersonChanger.noChange()), activities(), cars(), fixedDestinations(), opportunitys());
		serialiser.writeHeader();
		return serialiser;
	}

	public DemandDataDeserialiser deserialiseFromCsv() throws IOException {
		Deserialiser<HouseholdForSetup> households = deserialiseHousehold();
		Deserialiser<PersonPatternActivity> activities = deserialiseActivity();
		ForeignKeyDeserialiser<PersonBuilder> persons = deserialisePerson(personChanger);
		ForeignKeyDeserialiser<PrivateCarForSetup> cars = deserialiseCars();
		Deserialiser<PersonFixedDestination> fixedDestinations = deserialiseFixedDestinations();
		Deserialiser<Opportunity> opportunities = deserialiseOpportunities();
		return new DefaultDemandDataDeserialiser(households, persons, activities, cars,
				fixedDestinations, opportunities);
	}

	private Deserialiser<HouseholdForSetup> deserialiseHousehold() throws IOException {
		DefaultHouseholdFormat format = householdFormat();
		return deserialiser(household, format);
	}

	private Deserialiser<PersonPatternActivity> deserialiseActivity() throws IOException {
		DefaultActivityFormat format = activityFormat();
		return deserialiser(activity, format);
	}

	private ForeignKeyDeserialiser<PersonBuilder> deserialisePerson(PersonChanger personChanger)
			throws IOException {
		DefaultPersonFormat format = personFormat(personChanger);
		return deserialiser(person, format);
	}

	private ForeignKeyDeserialiser<PrivateCarForSetup> deserialiseCars() throws IOException {
		DefaultPrivateCarFormat format = privateCarFormat();
		return deserialiser(car, format);
	}

	private Deserialiser<PersonFixedDestination> deserialiseFixedDestinations() throws IOException {
		DefaultFixedDestinationFormat format = fixedDestinationFormat();
		return deserialiser(fixedDestination, format);
	}

	private Deserialiser<Opportunity> deserialiseOpportunities() throws IOException {
		return deserialiser(opportunity, opportunityFormat());
	}

	private <T> CsvSerialiser<T> serialiser(DemandDataInput input, SerialiserFormat<T> format)
			throws IOException {
		CSVWriter writer = input.writerIn(demandDataFolder);
		return new CsvSerialiser<>(writer, format);
	}

	private <T> CsvForeignKeySerialiser<T> serialiser(
			DemandDataInput input, ForeignKeySerialiserFormat<T> format) throws IOException {
		CSVWriter writer = input.writerIn(demandDataFolder);
		return new CsvForeignKeySerialiser<>(writer, format);
	}

	private <T> CsvDeserialiser<T> deserialiser(DemandDataInput input, SerialiserFormat<T> format)
			throws IOException {
		CSVReader reader = input.readerIn(demandDataFolder);
		return new CsvDeserialiser<>(reader, format);
	}

	private <T> CsvForeignKeyDeserialiser<T> deserialiser(
			DemandDataInput input, ForeignKeySerialiserFormat<T> format) throws IOException {
		CSVReader reader = input.readerIn(demandDataFolder);
		return new CsvForeignKeyDeserialiser<>(reader, format);
	}

	private DefaultHouseholdFormat householdFormat() {
		return formats.householdFormat();
	}

	private DefaultPersonFormat personFormat(PersonChanger personChanger) {
		return formats.personFormat(personChanger);
	}

	private DefaultActivityFormat activityFormat() {
		return formats.activityFormat();
	}

	private DefaultPrivateCarFormat privateCarFormat() {
		return formats.privateCarFormat();
	}

	private DefaultFixedDestinationFormat fixedDestinationFormat() {
		return formats.fixedDestinationFormat();
	}

	private DefaultOpportunityFormat opportunityFormat() {
		return formats.opportunityFormat();
	}

}
