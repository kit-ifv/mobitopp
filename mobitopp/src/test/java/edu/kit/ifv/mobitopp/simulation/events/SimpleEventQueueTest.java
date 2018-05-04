package edu.kit.ifv.mobitopp.simulation.events;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class SimpleEventQueueTest {

	private static final int priority = 0;
	private static final SimulationPerson person = mock(SimulationPerson.class);
	private static final OccupationIfc occupaction = mock(OccupationIfc.class);
	private static final Time early = Data.someTime();
	private static final Time late = early.plusHours(1);
	private EventQueue queue;
	private DemandSimulationEventIfc secondEvent;
	private DemandSimulationEventIfc firstEvent;

	@Before
	public void initialise() {
		firstEvent = new DemandSimulationEvent(priority, person, occupaction, early);
		secondEvent = new DemandSimulationEvent(priority, person, occupaction, late);
		queue = new SimpleEventQueue();
	}

	@Test
	public void processEachElementOneTime() {
		queue.add(firstEvent);
		queue.add(secondEvent);

		assertThat(queue.size(), is(2));
		assertThat(queue.hasEventsUntil(late), is(true));
		assertThat(queue.nextEvent(), is(sameInstance(firstEvent)));
		assertThat(queue.hasEventsUntil(late), is(true));
		assertThat(queue.nextEvent(), is(sameInstance(secondEvent)));
		assertThat(queue.hasEventsUntil(late), is(false));
		assertThat(queue.size(), is(0));
	}

	@Test
	public void processElementsOrderedPerTimeslot() {
		queue.add(secondEvent);
		queue.add(firstEvent);

		assertThat(queue.size(), is(2));
		assertThat(queue.hasEventsUntil(early), is(true));
		assertThat(queue.nextEvent(), is(sameInstance(firstEvent)));
		assertThat(queue.hasEventsUntil(early), is(false));
		assertThat(queue.hasEventsUntil(late), is(true));
		assertThat(queue.nextEvent(), is(sameInstance(secondEvent)));
		assertThat(queue.hasEventsUntil(late), is(false));
		assertThat(queue.size(), is(0));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addSameEventTwice() {
		queue.add(firstEvent);
		queue.add(firstEvent);
	}
}
