package edu.kit.ifv.mobitopp.populationsynthesis.commutation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;

@ExtendWith(MockitoExtension.class)
public class CommutingTimePredicateTest {

	@Mock
	private DemandRegion someRegion;
	@Mock
	private DemandRegion otherRegion;
	@Mock
	private DemandZone someZoneOne;
	@Mock
	private DemandZone someZoneTwo;
	@Mock
	private DemandZone otherZoneOne;
	@Mock
	private DemandZone otherZoneTwo;
	@Mock
	private ImpedanceIfc impedance;

	@BeforeEach
	public void initialise() {
		ZoneId someZoneOneId = new ZoneId("someZoneOne", 0);
		when(someZoneOne.getId()).thenReturn(someZoneOneId);
		ZoneId someZoneTwoId = new ZoneId("someZoneTwo", 1);
		when(someZoneTwo.getId()).thenReturn(someZoneTwoId);
		ZoneId otherZoneOneId = new ZoneId("otherZoneOne", 2);
		when(otherZoneOne.getId()).thenReturn(otherZoneOneId);
		ZoneId otherZoneTwoId = new ZoneId("otherZoneTwo", 3);
		when(otherZoneTwo.getId()).thenReturn(otherZoneTwoId);
		when(someRegion.zones()).then(invocation -> Stream.of(someZoneOne, someZoneTwo));
		when(otherRegion.zones()).then(invocation -> Stream.of(otherZoneOne, otherZoneTwo));
		when(impedance.getTravelTime(any(), any(), any(), any())).thenReturn(Float.MAX_VALUE);
		when(impedance.getTravelTime(someZoneOneId, otherZoneOneId, StandardMode.CAR, Time.start))
			.thenReturn(1.0f);
		when(impedance.getTravelTime(someZoneOneId, otherZoneTwoId, StandardMode.CAR, Time.start))
			.thenReturn(2.0f);
		when(impedance.getTravelTime(someZoneTwoId, otherZoneOneId, StandardMode.CAR, Time.start))
			.thenReturn(3.0f);
		when(impedance.getTravelTime(someZoneTwoId, otherZoneTwoId, StandardMode.CAR, Time.start))
			.thenReturn(4.0f);
	}

	@ParameterizedTest
	@CsvSource(value = { "1,false", "3,true" })
	void filterByMeanCommutingTime(int maxCommutingTime, boolean result) throws Exception {
		CommutingTimePredicate predicate = new CommutingTimePredicate(impedance, maxCommutingTime);

		boolean test = predicate.test(someRegion, otherRegion);

		assertThat(test).isEqualTo(result);
	}

}
