package edu.kit.ifv.mobitopp.data;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class ZoneTest {

	@MethodSource(value = "values")
	@ParameterizedTest
	void isDestinationButHasNoLocationForWork(
			final boolean isDestination, final Map<Location, Integer> locations, final boolean result)
			throws Exception {
		Zone zone = createZone(isDestination, locations);

		assertThat(zone.isDestination()).isEqualTo(isDestination);
		assertThat(zone.isDestinationFor(ActivityType.WORK)).isEqualTo(result);
	}

	private static Stream<Arguments> values() {
		Location dummyLocation = new Location(new Point2D.Double(), 0, 0);
		return Stream
				.of(Arguments.of(true, emptyMap(), false), //
						Arguments.of(true, Map.of(dummyLocation, 1), true), //
						Arguments.of(false, emptyMap(), false), //
						Arguments.of(false, Map.of(dummyLocation, 1), false));
	}

	private Zone createZone(boolean isDestination, Map<Location, Integer> locations) {
		ZoneId id = new ZoneId("1", 0);
		ZoneProperties properties = mock(ZoneProperties.class);
		when(properties.isDestination()).thenReturn(isDestination);
		Attractivities attractivities = mock(Attractivities.class);
		ChargingDataForZone charging = mock(ChargingDataForZone.class);
		Zone zone = new Zone(id, properties, attractivities, charging);
		zone.opportunities().createLocations((z, a, o) -> locations);
		return zone;
	}
}
