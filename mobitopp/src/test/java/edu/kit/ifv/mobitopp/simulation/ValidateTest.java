package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.data.DataSource;
import edu.kit.ifv.mobitopp.data.local.TemporaryFiles;

public class ValidateTest {

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();

	private File existingFile;
	private File nonExistingFile;
	private WrittenConfiguration configuration;
	private DataSource dataSource;

	@Before
	public void initialise() throws IOException {
		existingFile = baseFolder.newFile();
		nonExistingFile = TemporaryFiles.newMissingFile(baseFolder);
		dataSource = mock(DataSource.class);

		createValidConfiguration();
	}

	private void createValidConfiguration() {
		configuration = new WrittenConfiguration();
		configuration.setDestinationChoice(
				Collections.singletonMap("repetition", existingFile.getAbsolutePath()));
		configuration.setDataSource(dataSource);
	}

	@Test
	public void validateCorrectConfiguration() {
		validate();

		verify(dataSource).validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateIncorrectNumberOfZones() {
		int tooFew = 0;
		configuration.setNumberOfZones(tooFew);

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateTooLowFractionOfPopulation() {
		configuration.setFractionOfPopulation(-0.1f);

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateTooHighFractionOfPopulation() {
		configuration.setFractionOfPopulation(1.1f);

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void incorrectTimeStepLength() {
		configuration.setTimeStepLength(WrittenConfiguration.defaultTimeStepLength - 1);

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateDestinationChoiceFiles() {
		configuration.setDestinationChoice(
				Collections.singletonMap("repetition", nonExistingFile.getAbsolutePath()));

		validate();
	}

	private void validate() {
		new Validate().now(configuration);
	}
}
