package edu.kit.ifv.mobitopp.data.local.configuration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DayType;

public class TypeMatricesTest {

	private static final DayType defaultType = DayType.weekdays;
	private static final DayTypeMatrices defaultMatrices = new DayTypeMatrices();

	@Test
	public void useDefaultDayTypeIfRequestedIsMissing() {
		TypeMatrices modeMatrices = new TypeMatrices();
		modeMatrices.setAt(onlyDefaultMatrix());
		
		DayTypeMatrices matrices = modeMatrices.at(DayType.saturday);
		
		assertThat(matrices, is(sameInstance(defaultMatrices)));
	}

	private Map<DayType, DayTypeMatrices> onlyDefaultMatrix() {
		HashMap<DayType, DayTypeMatrices> matrices = new HashMap<>();
		matrices.put(defaultType, defaultMatrices);
		return matrices;
	}

	@Test(expected=IllegalStateException.class)
	public void failWithMissingDefaultMatrices() {
		TypeMatrices modeMatrices = new TypeMatrices();
		
		modeMatrices.at(defaultType);
	}
}
