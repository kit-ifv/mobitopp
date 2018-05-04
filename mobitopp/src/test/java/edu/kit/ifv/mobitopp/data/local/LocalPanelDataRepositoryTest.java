package edu.kit.ifv.mobitopp.data.local;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class LocalPanelDataRepositoryTest {

	private static final int aDomCode = 5;
	private static final int otherDomCode = 12;
	
	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();

	private HouseholdOfPanelDataId id;
	private InMemoryHouseholds households;
	private InMemoryPersons persons;
	private LocalPanelDataRepository repository;
	private File input;

	@Before
	public void initialise() throws IOException {
		id = mock(HouseholdOfPanelDataId.class);
		households = mock(InMemoryHouseholds.class);
		persons = mock(InMemoryPersons.class);
		createTestData();

		repository = new LocalPanelDataRepository(households, persons);
	}

	private void createTestData() throws IOException {
		input = baseFolder.newFile();
		Files.write(input.toPath(), ExamplePanel.content(), WRITE, CREATE);
	}

	@Test
	public void getHouseholds() {
		repository.getHousehold(id);

		verify(households).load(id);
	}

	@Test
	public void getHouseholdIds() {
		repository.getHouseholdIds(aDomCode);

		verify(households).getHouseholdIds(aDomCode);
	}

	@Test
	public void getPersonsOfHousehold() {
		repository.getPersonsOfHousehold(id);

		verify(persons).getPersonsOfHousehold(id);
	}

	@Test
	public void loadFromFile() {
		PanelDataRepository repository = LocalPanelDataRepository.loadFrom(input);

		assertThat(repository.getHouseholdIds(aDomCode), hasSize(1));
		assertThat(repository.getHouseholdIds(otherDomCode), hasSize(1));
	}

}
