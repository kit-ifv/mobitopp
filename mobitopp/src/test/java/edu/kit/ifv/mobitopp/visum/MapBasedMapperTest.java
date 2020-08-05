package edu.kit.ifv.mobitopp.visum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ZoneId;

public class MapBasedMapperTest {

	@Test
	void mapsToZoneId() throws Exception {
		Map<String, Integer> mapping = Map.of("1", 0, "2", 1);
		MapBasedMapper mapper = new MapBasedMapper(mapping);

		ZoneId first = mapper.mapToZoneId("1");
		ZoneId second = mapper.mapToZoneId("2");

		assertThat(first).isEqualTo(new ZoneId("1", 0));
		assertThat(second).isEqualTo(new ZoneId("2", 1));
	}
	
	@Test
	void failsForMissingId() throws Exception {
		MapBasedMapper mapper = new MapBasedMapper(Map.of());
		
		assertThrows(IllegalArgumentException.class, () -> mapper.map("1"));
	}
}
