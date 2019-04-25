package edu.kit.ifv.mobitopp.data.local;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;
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
    configureMatrix(TravelTimeMatrixType.car, carMatrix);
    configureMatrix(TravelTimeMatrixType.bike, bikeMatrix);
    TravelTimeMatrixCache cache = newCache();

    TravelTimeMatrix cachedCarMatrix = cache.matrixFor(Mode.CAR, SimpleTime.start);
    TravelTimeMatrix cachedBikeMatrix = cache.matrixFor(Mode.BIKE, SimpleTime.start);

    assertAll(() -> assertThat(cachedCarMatrix, is(sameInstance(carMatrix))),
        () -> assertThat(cachedBikeMatrix, is(sameInstance(bikeMatrix))));
  }

  @Test
  void matrixForMissingMode() throws Exception {
    TravelTimeMatrixCache cache = newCache();

    assertThrows(IllegalArgumentException.class,
        () -> cache.matrixFor(Mode.RIDE_HAILING, SimpleTime.start));
  }

  private void configureMatrix(TravelTimeMatrixType matrixType, TravelTimeMatrix matrix)
      throws IOException {
    TravelTimeMatrixId id = new TravelTimeMatrixId(matrixType, DayType.weekdays,
        TimeSpan.between(0, 1));
    when(configuration.idOf(matrixType, SimpleTime.start)).thenReturn(id);
    when(configuration.travelTimeMatrixFor(id)).thenReturn(new TaggedTravelTimeMatrix(id, matrix));
  }

  private TravelTimeMatrixCache newCache() {
    DynamicTypeMapping types = new DynamicTypeMapping();
    types.add(Mode.BIKE, TravelTimeMatrixType.bike);
    types.add(Mode.CAR, TravelTimeMatrixType.car);
    return new TravelTimeMatrixCache(configuration, types);
  }
}
