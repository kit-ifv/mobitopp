package edu.kit.ifv.mobitopp.data.local;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.TimeSpan;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.TypeMatrices;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public abstract class Valid {

	private static final DayType dayType = DayType.weekdays;
	private static final TimeSpan wholeDay = TimeSpan.between(0, 23);

	public static Map<CostMatrixType, TypeMatrices> costMatrices(String path) {
		return Arrays.stream(CostMatrixType.values()).collect(
				toMap(Function.identity(), type -> validMatrix(path)));
	}

	private static TypeMatrices validMatrix(String path) {
		TypeMatrices typeMatrices = new TypeMatrices();
		typeMatrices.add(dayType, wholeDay, path);
		return typeMatrices;
	}

	public static Map<ActivityType, String> fixedDistributionMatrices(String path) {
		return Arrays.stream(ActivityType.values()).collect(toMap(Function.identity(), type -> path));
	}

	public static Map<TravelTimeMatrixType, TypeMatrices> travelTimeMatrices(String path) {
		return Arrays.stream(TravelTimeMatrixType.values()).collect(
				toMap(Function.identity(), type -> validMatrix(path)));
	}
}
