package edu.kit.ifv.mobitopp.data.local;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedTravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TimeSpan;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixId;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.SimpleTime;

public class TravelTimeMatrixCacheTest {

	private TravelTimeMatrix carMatrix;
	private TravelTimeMatrix bikeMatrix;
	private MatrixConfiguration configuration;

	@BeforeEach
	public void initialise() {
		carMatrix = new TravelTimeMatrix(emptyList());
		bikeMatrix = new TravelTimeMatrix(emptyList());
		configuration = mock(MatrixConfiguration.class);
	}

	@Test
	void matrixForMode() throws Exception {
		configureMatrix(StandardMode.CAR, carMatrix);
		configureMatrix(StandardMode.BIKE, bikeMatrix);
		TravelTimeMatrixCache cache = newCache();

		TravelTimeMatrix cachedCarMatrix = cache.matrixFor(StandardMode.CAR, SimpleTime.start);
		TravelTimeMatrix cachedBikeMatrix = cache.matrixFor(StandardMode.BIKE, SimpleTime.start);

		assertAll(() -> assertThat(cachedCarMatrix, is(sameInstance(carMatrix))),
			() -> assertThat(cachedBikeMatrix, is(sameInstance(bikeMatrix))));
	}

	@Test
	void matrixForMissingMode() throws Exception {
		when(configuration.travelTimeMatrixFor(any())).thenThrow(IOException.class);
		TravelTimeMatrixCache cache = newCache();

		assertThrows(IllegalArgumentException.class,
			() -> cache.matrixFor(StandardMode.RIDE_HAILING, SimpleTime.start));
	}

	private void configureMatrix(StandardMode mode, TravelTimeMatrix matrix) throws IOException {
		TravelTimeMatrixId id = createIdFor(mode);
		when(configuration.idOf(mode, SimpleTime.start)).thenReturn(id);
		when(configuration.travelTimeMatrixFor(id))
			.thenReturn(new TaggedTravelTimeMatrix(id, matrix));
	}

	private TravelTimeMatrixId createIdFor(StandardMode mode) {
		return new TravelTimeMatrixId(mode, DayType.weekdays, TimeSpan.between(0, 1));
	}

	private TravelTimeMatrixCache newCache() {
		return new TravelTimeMatrixCache(configuration);
	}

}
