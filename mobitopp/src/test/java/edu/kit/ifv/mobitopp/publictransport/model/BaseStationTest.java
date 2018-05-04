package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class BaseStationTest {

	private static final int id = 0;
	
	private Collection<Node> nodes;
	private Node someNode;
	private Node anotherNode;
	private Station station;
	private Stop someStop;
	private Stop anotherStop;

	@Before
	public void initialise() throws Exception {
		someNode = mock(Node.class);
		anotherNode = mock(Node.class);
		nodes = asList(someNode, anotherNode);
		station = newStation();
		someStop = someStop();
		anotherStop = anotherStop();
		station.add(someStop);
		station.add(anotherStop);
	}
	
	@Test
	public void callsConsumerForEachStop() {
		Consumer<Stop> consumer = consumer();
		
		station.forEach(consumer);
		
		verify(consumer).accept(someStop);
		verify(consumer).accept(anotherStop);
	}

	@Test
	public void callsConsumerForEachNode() {
		Consumer<Node> consumer = consumer();

		station.forEachNode(consumer);

		verify(consumer).accept(someNode);
		verify(consumer).accept(anotherNode);
	}

	@SuppressWarnings("unchecked")
	private <T> T consumer() {
		return (T) mock(Consumer.class);
	}

	private BaseStation newStation() {
		return new BaseStation(id, nodes) {

			@Override
			public RelativeTime minimumChangeTime(int id) {
				throw new RuntimeException("Should never be called");
			}
		};
	}

}
