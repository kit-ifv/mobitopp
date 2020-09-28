package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;

@ExtendWith(MockitoExtension.class)
public class ZoneBasedRegionPredicateTest {

	@Mock
	private DemandRegion region;
	@Mock
	private DemandZone someZone;
	@Mock
	private DemandZone otherZone;
	
	@BeforeEach
	public void initialise() {
		when(region.zones()).thenReturn(Stream.of(someZone, otherZone));
	}

	@Test
	void selectsRegionWhenAllZonesShouldBeGenerated() throws Exception {
		when(someZone.shouldGeneratePopulation()).thenReturn(true);
		when(otherZone.shouldGeneratePopulation()).thenReturn(true);
		ZoneBasedRegionPredicate predicate = new ZoneBasedRegionPredicate();
		
		boolean test = predicate.test(region);
		
		assertThat(test).isTrue();
	}

	@Test
	void filtersRegionWhenAllZonesShouldBeGenerated() throws Exception {
		when(someZone.shouldGeneratePopulation()).thenReturn(true);
		when(otherZone.shouldGeneratePopulation()).thenReturn(false);
		ZoneBasedRegionPredicate predicate = new ZoneBasedRegionPredicate();
		
		boolean test = predicate.test(region);
		
		assertThat(test).isFalse();
	}
}
