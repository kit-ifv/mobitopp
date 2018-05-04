package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_OCCUP;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_PRIMARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_SECONDARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_TERTIARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.DayTypeMatrices;
import edu.kit.ifv.mobitopp.data.local.configuration.StoredMatrices;
import edu.kit.ifv.mobitopp.data.local.configuration.StoredMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TimeSpan;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.TypeMatrices;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class MatrixValidator {

	private static final TimeSpan wholeDay = TimeSpan.between(0, 23);

	private final StoredMatrices matrices;
	private final File baseFolder;

	public MatrixValidator(StoredMatrices matrices, File baseFolder) {
		super();
		this.matrices = matrices;
		this.baseFolder = baseFolder;
	}

	public void areValid() {
		verifyCostMatrices();
		verifyFixedDistributionMatrices();
		verifyTravelTimeMatrices();
		verifyFiles();
	}

	private void verifyFiles() {
		matrices.forEach(this::validate);
	}

	private void validate(StoredMatrix matrix) {
		Validate.files(matrix.file(baseFolder)).doExist();
	}

	private void verifyTravelTimeMatrices() {
		List<String> missingMatrices = missingTravelTimeMatrices();
		verify(missingMatrices, "Travel time");
	}

	private List<String> missingTravelTimeMatrices() {
		List<String> missingMatrices = new LinkedList<>();
		for (TravelTimeMatrixType type : travelTimeTypes()) {
			TypeMatrices typeMatrices = matrices.travelTimeMatrixFor(type);
			Consumer<String> matrixCollector = text -> missingMatrices.add(type + " - " + text);
			collectPerDay(typeMatrices, matrixCollector);
		}
		return missingMatrices;
	}

	private Collection<TravelTimeMatrixType> travelTimeTypes() {
		return EnumSet.complementOf(EnumSet.of(TravelTimeMatrixType.truck));
	}

	private void verifyFixedDistributionMatrices() {
		List<String> missingMatrices = missingFixedDistributionMatrices();
		verify(missingMatrices, "Fixed distribution");
	}

	private List<String> missingFixedDistributionMatrices() {
		List<String> missingMatrices = new LinkedList<>();
		for (ActivityType type : fixedDistributionTypes()) {
			try {
				matrices.fixedDistributionMatrixFor(type);
			} catch (IllegalArgumentException cause) {
				missingMatrices.add(type.name());
			}
		}
		return missingMatrices;
	}

	private Collection<ActivityType> fixedDistributionTypes() {
		return EnumSet.of(EDUCATION, EDUCATION_OCCUP, EDUCATION_PRIMARY, EDUCATION_SECONDARY,
				EDUCATION_TERTIARY, WORK);
	}

	private void verifyCostMatrices() {
		List<String> missingMatrices = missingCostMatrices();
		verify(missingMatrices, "Cost");
	}

	private void verify(List<String> missingMatrices, String type) {
		if (missingMatrices.isEmpty()) {
			return;
		}
		throw new IllegalArgumentException(type + " matrices are not specified: " + missingMatrices);
	}

	private List<String> missingCostMatrices() {
		List<String> missingMatrices = new LinkedList<>();
		for (CostMatrixType type : CostMatrixType.values()) {
			TypeMatrices typeMatrices = matrices.costMatrixFor(type);
			Consumer<String> matrixCollector = text -> missingMatrices.add(type + " - " + text);
			collectPerDay(typeMatrices, matrixCollector);
		}
		return missingMatrices;
	}

	private void collectPerDay(TypeMatrices typeMatrices, Consumer<String> matrixCollector) {
		for (DayType dayType : DayType.values()) {
			DayTypeMatrices dayTypeMatrices = typeMatrices.at(dayType);
			if (coversOnlyPartDay(dayTypeMatrices)) {
				matrixCollector.accept(dayType.name());
			}
		}
	}

	private boolean coversOnlyPartDay(DayTypeMatrices dayTypeMatrices) {
		return !wholeDay.equals(dayTypeMatrices.coveredTime());
	}

}
