package edu.kit.ifv.mobitopp.simulation.events;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class DemandSimulationEventTest {

	private static final Time sameDate = Data.someTime();
	private static final Time earlierDate = Data.someTime();
	private static final Time laterDate = earlierDate.plusSeconds(1);

	private SimulationPerson person;
	private OccupationIfc occupation;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void initialise() {
		person = mock(SimulationPerson.class);
		occupation = mock(OccupationIfc.class);

		when(person.getOid()).thenReturn(1);
		when(occupation.getOid()).thenReturn(1);
	}

	@Test
	public void compareDate() {
		assertThat(earlierEvent().compareTo(laterEvent()), is(lessThan(0)));
		assertThat(earlierEvent().compareTo(earlierEvent()), is(equalTo(0)));
		assertThat(laterEvent().compareTo(earlierEvent()), is(greaterThan(0)));
	}

	@Test
	public void comparePriority() {
		assertThat(higherPriority().compareTo(lowerPriority()), is(lessThan(0)));
		assertThat(lowerPriority().compareTo(lowerPriority()), is(equalTo(0)));
		assertThat(lowerPriority().compareTo(higherPriority()), is(greaterThan(0)));
	}

	@Test
	public void comparePersonId() {
		assertThat(lowPersonId().compareTo(highPersonId()), is(lessThan(0)));
		assertThat(lowPersonId().compareTo(lowPersonId()), is(equalTo(0)));
		assertThat(highPersonId().compareTo(lowPersonId()), is(greaterThan(0)));
	}

	@Test
	public void compareOccupationId() {
		assertThat(lowOccupationId().compareTo(highOccupationId()), is(lessThan(0)));
		assertThat(lowOccupationId().compareTo(lowOccupationId()), is(equalTo(0)));
		assertThat(highOccupationId().compareTo(lowOccupationId()), is(greaterThan(0)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyPostCondition() {
		new DemandSimulationEvent(person, occupation, null);
	}

	private DemandSimulationEvent lowPersonId() {
		SimulationPerson person = mock(SimulationPerson.class);
		when(person.getOid()).thenReturn(1);
		return new DemandSimulationEvent(person, occupation, laterDate);
	}

	private DemandSimulationEvent highPersonId() {
		SimulationPerson person = mock(SimulationPerson.class);
		when(person.getOid()).thenReturn(2);
		return new DemandSimulationEvent(person, occupation, laterDate);
	}

	private DemandSimulationEvent lowOccupationId() {
		OccupationIfc occupation = mock(OccupationIfc.class);
		when(occupation.getOid()).thenReturn(1);
		return new DemandSimulationEvent(person, occupation, laterDate);
	}

	private DemandSimulationEvent highOccupationId() {
		OccupationIfc occupation = mock(OccupationIfc.class);
		when(occupation.getOid()).thenReturn(2);
		return new DemandSimulationEvent(person, occupation, laterDate);
	}

	private DemandSimulationEvent laterEvent() {
		return new DemandSimulationEvent(person, occupation, laterDate);
	}

	private DemandSimulationEvent earlierEvent() {
		return new DemandSimulationEvent(person, occupation, earlierDate);
	}

	private DemandSimulationEvent higherPriority() {
		return new DemandSimulationEvent(10, person, occupation, sameDate);
	}

	private DemandSimulationEvent lowerPriority() {
		return new DemandSimulationEvent(20, person, occupation, sameDate);
	}
}
