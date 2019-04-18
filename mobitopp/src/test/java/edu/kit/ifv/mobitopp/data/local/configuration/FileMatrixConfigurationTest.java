package edu.kit.ifv.mobitopp.data.local.configuration;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.data.local.MatrixParser;
import edu.kit.ifv.mobitopp.data.local.Valid;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;

public class FileMatrixConfigurationTest {

	private static final CostMatrixType matrixType = CostMatrixType.car;
	private static final TravelTimeMatrixType travelTimeType = TravelTimeMatrixType.car;
	private static final Time weekday = Data.someTime();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private MatrixParser parser;
	private File baseFolder;
	private File existingFile;
	private MatrixConfiguration configuration;

	@Before
	public void initialise() throws IOException {
		parser = mock(MatrixParser.class);
		baseFolder = temporaryFolder.getRoot();
		existingFile = temporaryFolder.newFile();
		configuration = configuration(storedMatrices());
	}

	private MatrixConfiguration configuration(StoredMatrices stored) {
		IdToOidMapper idToOidMapper = Integer::valueOf;
    return new FileMatrixConfiguration(stored, baseFolder, idToOidMapper) {

			@Override
			MatrixParser parserFor(StoredMatrix storedMatrix) throws FileNotFoundException {
				return parser;
			}
		};
	}

	private StoredMatrices storedMatrices() {
		StoredMatrices matrices = new StoredMatrices();
		matrices.setCostMatrices(Valid.costMatrices(fileName()));
		matrices.setFixedDistributionMatrices(Valid.fixedDistributionMatrices(fileName()));
		matrices.setTravelTimeMatrices(Valid.travelTimeMatrices(fileName()));
		return matrices;
	}

	private String fileName() {
		return existingFile.getName();
	}

	@Test
	public void loadCostMatrix() throws IOException {
		CostMatrixId matrixId = configuration.idOf(matrixType, weekday);
		configuration.costMatrixFor(matrixId);

		verify(parser).parseCostMatrix();
	}

	@Test
	public void loadFixedDistributionMatrix() throws IOException {
		FixedDistributionMatrixId matrixId = new FixedDistributionMatrixId(WORK);
		configuration.fixedDistributionMatrixFor(matrixId);

		verify(parser).parseFixedDistributionMatrix();
	}

	@Test
	public void loadTravelTimeMatrix() throws IOException {
		TravelTimeMatrixId matrixId = configuration.idOf(travelTimeType, weekday);
		configuration.travelTimeMatrixFor(matrixId);

		verify(parser).parseTravelTimeMatrix();
	}
	
	@Test
	public void validateExistingMatrices() {
		configuration.validate();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void validateMissingBaseFolder() throws IOException {
		MatrixConfiguration configuration = FileMatrixConfiguration.empty(missingFolder());
		
		configuration.validate();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void validateMissingCostMatrix() {
		MatrixConfiguration configuration = configuration(missingCostMatrix());
		
		configuration.validate();
	}

	private StoredMatrices missingCostMatrix() {
		StoredMatrices storedMatrices = storedMatrices();
		storedMatrices.setCostMatrices(emptyMap());
		return storedMatrices;
	}

	private File missingFolder() throws IOException {
		File newFolder = temporaryFolder.newFolder();
		newFolder.delete();
		assertFalse(newFolder.exists());
		return newFolder;
	}

}
