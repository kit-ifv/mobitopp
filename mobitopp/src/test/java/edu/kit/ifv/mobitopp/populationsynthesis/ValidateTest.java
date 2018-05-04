package edu.kit.ifv.mobitopp.populationsynthesis;

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

	private WrittenConfiguration configuration;
	private File existingFile;
	private File nonExistingFile;
	private DataSource dataSource;

	@Before
	public void initialise() throws IOException {
		existingFile = baseFolder.newFile();
		nonExistingFile = TemporaryFiles.newMissingFile(baseFolder);
		dataSource = mock(DataSource.class);

		createValidConfiguration();
	}

	private void createValidConfiguration() {
		CarOwnership ownership = new CarOwnership();
		ownership.setEngine(existingFile.getAbsolutePath());
		ownership.setOwnership(existingFile.getAbsolutePath());
		ownership.setSegment(existingFile.getAbsolutePath());
		configuration = new WrittenConfiguration();
		configuration.setVisumFile(existingFile.getAbsolutePath());
		configuration.setCarOwnership(ownership);
		configuration.setCommuterTicket(existingFile.getAbsolutePath());
		configuration.setDataSource(dataSource);
		configuration.setDemographyData(existingFile.getAbsolutePath());
		configuration.setPanelData(existingFile.getAbsolutePath());
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
	public void validateMissingNetworkFile() {
		configuration.setVisumFile(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingCarEngineFile() {
		configuration.getCarOwnership().setEngine(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingCarOwnershipFile() {
		configuration.getCarOwnership().setOwnership(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingCarSegmentFile() {
		configuration.getCarOwnership().setSegment(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateCarSharingFiles() {
		configuration
				.setCarSharing(Collections.singletonMap("non-existing", nonExistingFile.getAbsolutePath()));

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateCommuterTicketFile() {
		configuration.setCommuterTicket(nonExistingFile.getAbsolutePath());

		validate();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void validateDemographyDataFile() {
		configuration.setDemographyData(nonExistingFile.getAbsolutePath());
		
		validate();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void validatePanelDataFile() {
		configuration.setPanelData(nonExistingFile.getAbsolutePath());
		
		validate();
	}

	private void validate() {
		new Validate().now(configuration);
	}

}
