package edu.kit.ifv.mobitopp.data.local;

import static java.util.Collections.singletonMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.StoredMatrices;
import edu.kit.ifv.mobitopp.data.local.configuration.TimeSpan;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.TypeMatrices;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class MatrixValidatorTest {

	private static final DayType dayType = DayType.weekdays;
	private static final TimeSpan wholeDay = TimeSpan.between(0, 23);

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private StoredMatrices matrices;
	private File baseFolder;
	private File existingFile;
	private File nonExistingFile;

	@Before
	public void initialise() throws IOException {
		matrices = new StoredMatrices();
		baseFolder = temporaryFolder.getRoot();
		existingFile = temporaryFolder.newFile();
		nonExistingFile = TemporaryFiles.newMissingFile(temporaryFolder);
		setValidMatrices();
	}

	@Test
	public void validateExistingMatrices() {
		matrices().areValid();
	}

	private MatrixValidator matrices() {
		return new MatrixValidator(matrices, baseFolder);
	}

	private void setValidMatrices() {
		matrices.setCostMatrices(Valid.costMatrices(existingPath()));
		matrices.setFixedDistributionMatrices(Valid.fixedDistributionMatrices(existingPath()));
		matrices.setTravelTimeMatrices(Valid.travelTimeMatrices(existingPath()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingCostMatrices() {
		missingCostMatrix();
		matrices().areValid();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingFixedDistributionMatrices() {
		matrices.setFixedDistributionMatrices(missingFixedDistributionMatrix());
		matrices().areValid();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateMissingTravelTimeMatrices() {
		matrices.setTravelTimeMatrices(missingTravelTimeMatrix());
		matrices().areValid();
	}

	private void missingCostMatrix() {
		matrices.add(CostMatrixType.car, dayType, wholeDay, nonExistingPath());
	}

	private Map<ActivityType, String> missingFixedDistributionMatrix() {
		return singletonMap(ActivityType.WORK, nonExistingPath());
	}

	private Map<TravelTimeMatrixType, TypeMatrices> missingTravelTimeMatrix() {
		return singletonMap(TravelTimeMatrixType.bike, missingMatrix());
	}

	private String nonExistingPath() {
		return nonExistingFile.getName();
	}

	private TypeMatrices missingMatrix() {
		TypeMatrices typeMatrices = new TypeMatrices();
		typeMatrices.add(dayType, wholeDay, existingPath());
		return typeMatrices;
	}

	private String existingPath() {
		return existingFile.getName();
	}
}
