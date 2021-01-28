package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Collections.singletonMap;
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
import edu.kit.ifv.mobitopp.data.local.DynamicTypeMapping;
import edu.kit.ifv.mobitopp.data.local.TemporaryFiles;

public class ValidateTest {

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();

	private WrittenConfiguration configuration;
	private File existingFile;
	private File nonExistingFile;
	private DataSource dataSource;

	private DynamicTypeMapping modeToType;

	@Before
	public void initialise() throws IOException {
		existingFile = baseFolder.newFile();
		nonExistingFile = TemporaryFiles.newMissingFile(baseFolder);
		dataSource = mock(DataSource.class);
		modeToType = new DynamicTypeMapping();
		

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
		configuration.setDemographyData(singletonMap("some", existingFile.getAbsolutePath()));
		configuration.setPanelData(existingFile.getAbsolutePath());
	}

	@Test
	public void validateCorrectConfiguration() throws IOException {
		validate();

		verify(dataSource).validate(modeToType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateIncorrectNumberOfZones() throws IOException {
		int tooFew = 0;
		configuration.setNumberOfZones(tooFew);

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingNetworkFile() throws IOException {
		configuration.setVisumFile(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingCarEngineFile() throws IOException {
		configuration.getCarOwnership().setEngine(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingCarOwnershipFile() throws IOException {
		configuration.getCarOwnership().setOwnership(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingCarSegmentFile() throws IOException {
		configuration.getCarOwnership().setSegment(nonExistingFile.getAbsolutePath());

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMobilityProviderFiles() throws IOException {
		configuration
				.setMobilityProviders(Collections.singletonMap("non-existing", nonExistingFile.getAbsolutePath()));

		validate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateCommuterTicketFile() throws IOException {
		configuration.setCommuterTicket(nonExistingFile.getAbsolutePath());

		validate();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void validateDemographyDataFile() throws IOException {
		configuration.setDemographyData(singletonMap("missing", nonExistingFile.getAbsolutePath()));
		
		validate();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void validatePanelDataFile() throws IOException {
		configuration.setPanelData(nonExistingFile.getAbsolutePath());
		
		validate();
	}

	private void validate() throws IOException {
		new Validate(modeToType).now(configuration);
	}

}
