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
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class DemandDataFolder {

	private final File demandDataFolder;
	private final InputFormats formats;

	private DemandDataFolder(File demandDataFolder, InputFormats formats) {
		super();
		this.demandDataFolder = demandDataFolder;
		this.formats = formats;
	}

	public static DemandDataFolder at(
			File demandDataFolder, ZoneRepository zoneRepository, ZoneRepository zonesToSimulate)
			throws IOException {
		initialiseDirectory(demandDataFolder);
		InputFormats formats = new InputFormats(zoneRepository, zonesToSimulate);
		return new DemandDataFolder(demandDataFolder, formats);
	}

	private static void initialiseDirectory(File folder) {
		if (folder.isFile()) {
			throw new IllegalArgumentException(
					"Path does not point to a directory: " + folder.getAbsolutePath());
		}
		folder.mkdirs();
	}

	private Serialiser<Household> households() throws IOException {
		DefaultHouseholdFormat householdFormat = householdFormat();
		return serialiser(household, householdFormat);
	}

	private ForeignKeySerialiser<Person> persons() throws IOException {
		DefaultPersonFormat format = personFormat();
		return serialiser(person, format);
	}

	private Serialiser<PersonPatternActivity> activities() throws IOException {
		DefaultActivityFormat format = activityFormat();
		return serialiser(activity, format);
	}

	private ForeignKeySerialiser<PrivateCar> cars() throws IOException {
		DefaultPrivateCarFormat format = privateCarFormat();
		return serialiser(car, format);
	}

	private ForeignKeySerialiser<PersonFixedDestination> fixedDestinations() throws IOException {
		DefaultFixedDestinationFormat format = fixedDestinationFormat();
		return serialiser(fixedDestination, format);
	}

	private Serialiser<Opportunity> opportunitys() throws IOException {
		DefaultOpportunityFormat format = opportunityFormat();
		return serialiser(opportunity, format);
	}

	public DemandDataSerialiser serialiseAsCsv() throws IOException {
		return new DefaultDemandDataSerialiser(households(), persons(), activities(), cars(),
				fixedDestinations(), opportunitys());
	}

	public DemandDataDeserialiser deserialiseFromCsv() throws IOException {
		Deserialiser<Household> households = deserialiseHousehold();
		Deserialiser<PersonPatternActivity> activities = deserialiseActivity();
		ForeignKeyDeserialiser<Person> persons = deserialisePerson();
		ForeignKeyDeserialiser<PrivateCar> cars = deserialiseCars();
		ForeignKeyDeserialiser<PersonFixedDestination> fixedDestinations = deserialiseFixedDestinations();
		Deserialiser<Opportunity> opportunities = deserialiseOpportunities();
		return new DefaultDemandDataDeserialiser(households, persons, activities, cars,
				fixedDestinations, opportunities );
	}

	private Deserialiser<Household> deserialiseHousehold() throws IOException {
		DefaultHouseholdFormat format = householdFormat();
		return deserialiser(household, format);
	}

	private Deserialiser<PersonPatternActivity> deserialiseActivity() throws IOException {
		DefaultActivityFormat format = activityFormat();
		return deserialiser(activity, format);
	}

	private ForeignKeyDeserialiser<Person> deserialisePerson() throws IOException {
		DefaultPersonFormat format = personFormat();
		return deserialiser(person, format);
	}

	private ForeignKeyDeserialiser<PrivateCar> deserialiseCars() throws IOException {
		DefaultPrivateCarFormat format = privateCarFormat();
		return deserialiser(car, format);
	}

	private ForeignKeyDeserialiser<PersonFixedDestination> deserialiseFixedDestinations()
			throws IOException {
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

	private DefaultPersonFormat personFormat() {
		return formats.personFormat();
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
