package edu.kit.ifv.mobitopp.routing;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import edu.kit.ifv.mobitopp.routing.util.PriorityQueueBasedPQ;
import edu.kit.ifv.mobitopp.routing.util.SimplePQ;
import edu.kit.ifv.mobitopp.routing.util.TreeBasedPQ;

@RunWith(Parameterized.class)
public class PriorityQueueTest {

	@Parameters(name = "{index} - {0}")
	public static Iterable<Supplier<PriorityQueue<Integer>>> queues() {
		ArrayList<Supplier<PriorityQueue<Integer>>> queues = new ArrayList<>();
		queues.add(SimplePQ::new);
		queues.add(PriorityQueueBasedPQ::new);
		queues.add(TreeBasedPQ::new);
		return queues;
	}
	
	@Parameter
	public Supplier<PriorityQueue<Integer>> factory;
	
	private PriorityQueue<Integer> queue;
	
	@Before 
	public void buildQueue() {
		queue = factory.get();
	}

	@Test 
	public void testDummy() {
		assertEquals("Dummy", 1, 1);
	}

	@Test(expected=java.lang.AssertionError.class)
	public void testAssert() {
		assert false;
	}

	@Test 
	public void add() {
		queue.add(12, 0.5f);
		assertEquals(new Integer(12), queue.minElement());

		queue.add( 7, 0.3f);
		assertEquals(new Integer(7), queue.minElement());

		queue.add( 3, 1.0f);
		assertEquals(new Integer(7), queue.minElement());

	}

	@Test 
	public void remove() {
		queue.add(12, 0.5f);
		queue.add( 7, 0.3f);
		queue.add( 3, 1.0f);

		assertEquals(new Integer(7), queue.minElement());

		queue.deleteMin();
		assertEquals(new Integer(12), queue.minElement());


		queue.deleteMin();
		assertEquals(new Integer(3), queue.minElement());
	}

	@Test 
	public void priority() {
		queue.add(12, 0.5f);
		assertEquals(0.5f, queue.minPriority(), 0.001f);

		queue.add( 7, 0.3f);
		assertEquals(0.3f, queue.minPriority(), 0.001f);

		queue.add( 3, 1.0f);
		assertEquals(0.3f, queue.minPriority(), 0.001f);
	}

	@Test 
	public void decreaseKey() {
		queue.add(12, 0.5f);
		assertEquals(new Integer(12), queue.minElement());

		queue.add( 7, 0.3f);
		assertEquals(new Integer(7), queue.minElement());

		queue.add( 3, 1.0f);
		assertEquals(new Integer(7), queue.minElement());

		queue.decreaseKey(3, 0.1f);
		assertEquals(new Integer(3), queue.minElement());
	}

	@Test 
	public void duplicatePriority() {
		queue.add(12, 0.5f);
		queue.add( 7, 0.3f);
		queue.add( 3, 0.3f);

		assertEquals(0.3f, queue.minPriority(), 0.001f);
		assertEquals(3, queue.size());
	}

	@Test 
	public void duplicateValue() {
		queue.add(12, 0.5f);
		queue.add( 7, 0.3f);
		queue.add( 3, 1.0f);
		queue.add( 7, 0.1f);

		assertThat(queue.size(), is(3));
		float margin = 1e-3f;
		assertEquals(0.1f, queue.minPriority(), margin);
		assertThat(queue.deleteMin(), is(7));
		assertThat(queue.deleteMin(), is(12));
		assertThat(queue.deleteMin(), is(3));
	}

}
