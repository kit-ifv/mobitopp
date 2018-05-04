package edu.kit.ifv.mobitopp.data.local.configuration;

import static edu.kit.ifv.mobitopp.data.local.configuration.TimeSpan.between;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class MatrixConfigurationSerialiserTest {

	private static final String path = "C:\\path\\to\\matrix.jrta";

	@Test
	public void serialisesMatrices() {
		StoredMatrices matrices = storedMatrices();

		String serialised = serialise(matrices);

		assertEquals(serialised, simpleSerialisedMatrices());
	}

	private StoredMatrices storedMatrices() {
		StoredMatrices matrices = new StoredMatrices();
		matrices.setCostMatrices(costMatrices());
		matrices.setFixedDistributionMatrices(fixedDistributionMatrices());
		matrices.setTravelTimeMatrices(travelTimeMatrices());
		return matrices;
	}

	private Map<CostMatrixType, TypeMatrices> costMatrices() {
		Map<CostMatrixType, TypeMatrices> costMatrices = new LinkedHashMap<>();
		costMatrices.put(CostMatrixType.car, modeMatrices());
		costMatrices.put(CostMatrixType.publictransport, modeMatrices());
		return costMatrices;
	}

	private Map<ActivityType, String> fixedDistributionMatrices() {
		Map<ActivityType, String> matrices = new LinkedHashMap<>();
		matrices.put(ActivityType.WORK, path);
		matrices.put(ActivityType.HOME, path);
		return matrices;
	}

	private Map<TravelTimeMatrixType, TypeMatrices> travelTimeMatrices() {
		Map<TravelTimeMatrixType, TypeMatrices> costMatrices = new LinkedHashMap<>();
		costMatrices.put(TravelTimeMatrixType.car, modeMatrices());
		costMatrices.put(TravelTimeMatrixType.publictransport, modeMatrices());
		return costMatrices;
	}

	@Test
	public void loadConfiguration() {
		StoredMatrices stored = storedMatrices();
		String serialised = serialise(stored);
		MatrixConfigurationSerialiser serialiser = new MatrixConfigurationSerialiser();

		StoredMatrices loaded = serialiser.loadFrom(input(serialised));

		assertThat(loaded, is(equalTo(stored)));
	}

	private String serialise(StoredMatrices stored) {
		return new MatrixConfigurationSerialiser().serialise(stored);
	}

	private InputStream input(String serialised) {
		return new ByteArrayInputStream(serialised.getBytes());
	}

	private TypeMatrices modeMatrices() {
		Map<DayType, DayTypeMatrices> matrices = new LinkedHashMap<>();
		matrices.put(DayType.weekdays, dayTypeMatrices());
		matrices.put(DayType.saturday, dayTypeMatrices());
		TypeMatrices modeMatrices = new TypeMatrices();
		modeMatrices.setAt(matrices);
		return modeMatrices;
	}

	private DayTypeMatrices dayTypeMatrices() {
		Map<TimeSpan, String> matrices = new LinkedHashMap<>();
		matrices.put(between(0, 0), matrix());
		matrices.put(between(1, 2), matrix());
		DayTypeMatrices dayTypeMatrices = new DayTypeMatrices();
		dayTypeMatrices.setBetween(matrices);
		return dayTypeMatrices;
	}

	private String matrix() {
		return path;
	}

	private String simpleSerialisedMatrices() {
		return "costMatrices:\n"
				+ "  car:\n"
				+ "    at:\n"
				+ "      weekdays:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n"
				+ "      saturday:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n"
				+ "  publictransport:\n"
				+ "    at:\n"
				+ "      weekdays:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n"
				+ "      saturday:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n"
				+ "fixedDistributionMatrices:\n"
				+ "  WORK: C:\\path\\to\\matrix.jrta\n"
				+ "  HOME: C:\\path\\to\\matrix.jrta\n"
				+ "travelTimeMatrices:\n"
				+ "  car:\n"
				+ "    at:\n"
				+ "      weekdays:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n"
				+ "      saturday:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n"
				+ "  publictransport:\n"
				+ "    at:\n"
				+ "      weekdays:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n"
				+ "      saturday:\n"
				+ "        between:\n"
				+ "          0 to 0: C:\\path\\to\\matrix.jrta\n"
				+ "          1 to 2: C:\\path\\to\\matrix.jrta\n";
	}
}
