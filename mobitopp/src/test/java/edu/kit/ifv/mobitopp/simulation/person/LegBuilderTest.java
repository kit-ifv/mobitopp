package edu.kit.ifv.mobitopp.simulation.person;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class LegBuilderTest {

	private LegBuilder builder;
	private Consumer<PublicTransportLeg> consumer;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		builder = new LegBuilder();
		consumer = mock(Consumer.class);
	}

	@Test
	public void buildsSimpleLeg() {
		builder.startWith(someConnection());
		builder.createLeg(consumer);

		verify(consumer).accept(simpleLeg());
	}

	private PublicTransportLeg simpleLeg() {
		Stop start = someConnection().start();
		Stop end = someConnection().end();
		Journey journey = someConnection().journey();
		Time departure = someConnection().departure();
		Time arrival = someConnection().arrival();
		List<Connection> connections = asList(someConnection());
		return new PublicTransportLeg(start, end, journey, departure, arrival, connections);
	}

	private Connection someConnection() {
		return Data.fromSomeToAnother();
	}

	@Test
	public void buildsBiggerLeg() {
		builder.startWith(someConnection());
		builder.add(anotherConnection());
		builder.createLeg(consumer);

		verify(consumer).accept(complexLeg());
	}

	private PublicTransportLeg complexLeg() {
		Stop start = someConnection().start();
		Stop end = anotherConnection().end();
		Journey journey = someConnection().journey();
		Time departure = someConnection().departure();
		Time arrival = anotherConnection().arrival();
		List<Connection> connections = asList(someConnection(), anotherConnection());
		return new PublicTransportLeg(start, end, journey, departure, arrival, connections);
	}

	private Connection anotherConnection() {
		return Data.fromAnotherToOther();
	}

	@Test
	public void buildsLegBeforeStarted() {
		builder.createLeg(consumer);

		verifyZeroInteractions(consumer);
	}

	@Test
	public void splitsOnDifferentJourney() {
		builder.startWith(someConnection());

		boolean needsToSplit = builder.needsToSplit(Data.fromAnotherToOtherByOtherJourney());

		assertTrue(needsToSplit);
	}

	@Test
	public void doesNotSplitOnSameJourney() {
		builder.startWith(someConnection());
		boolean needsToSplit = builder.needsToSplit(anotherConnection());

		assertFalse(needsToSplit);
	}

	@Test
	public void splitsBeforeStarted() {
		boolean needsToSplit = builder.needsToSplit(Data.fromAnotherToOtherByOtherJourney());

		assertTrue(needsToSplit);
	}

}
