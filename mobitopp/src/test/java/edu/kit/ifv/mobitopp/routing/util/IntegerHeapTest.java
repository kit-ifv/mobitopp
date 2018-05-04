package edu.kit.ifv.mobitopp.routing.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;

public class IntegerHeapTest {

	IntegerHeap h;

	@Before 
	public void setUp() {

		h = new IntegerHeap();

	}

	@Test 
	public void testConstructor() {
	
		assertEquals(0, h.size());
	}

	@Test 
	public void testLeft() {
	
		assertEquals( 1, h.left(0));
		assertEquals( 3, h.left(1));
		assertEquals( 7, h.left(3));
 
		assertEquals( 5, h.left(2));
		assertEquals(11, h.left(5));
	}

	@Test 
	public void testRight() {
	
		assertEquals( 2, h.right(0));
		assertEquals( 6, h.right(2));
		assertEquals(14, h.right(6));

		assertEquals( 4, h.right(1));
		assertEquals(10, h.right(4));
	}


	@Test 
	public void testParent() {
	
		assertEquals( 0, h.parent(1));
		assertEquals( 0, h.parent(2));

		assertEquals( 1, h.parent(3));
		assertEquals( 1, h.parent(4));

		assertEquals( 2, h.parent(5));
		assertEquals( 2, h.parent(6));

		assertEquals( 3, h.parent(8));
		assertEquals( 4, h.parent(9));
		assertEquals( 5, h.parent(12));
		assertEquals( 6, h.parent(13));
	}

	@Test 
	public void testInsert() {

		h.add(1, 1.0f);
		h.add(2, 2.0f);
		h.add(3, 3.0f);
		h.add(4, 4.0f);

		assertEquals(4, h.size());
		assertTrue(h.isHeap(0));
		assertEquals(0.0001, 1.0f, h.minPriority());
		assertEquals(1, (int) h.minElement());
	}

	@Test 
	public void testInsertOther() {

		h.add(4, 4.0f);
		h.add(3, 3.0f);
		h.add(2, 2.0f);
		h.add(1, 1.0f);

		assertEquals(4, h.size());
		assertTrue(h.isHeap(0));
		assertEquals(0.0001, 1.0f, h.minPriority());
		assertEquals(1, (int) h.minElement());
	}

	@Test 
	public void testDeleteMin() {

		h.add(4, 4.0f);
		h.add(3, 3.0f);
		h.add(2, 2.0f);
		h.add(1, 1.0f);

		Integer min = (int) h.deleteMin();

		assertEquals(1, (int) min);
		assertEquals(2, (int) h.minElement());

		min = (int) h.deleteMin();
		assertEquals(2, (int) min);
		assertEquals(3, (int) h.minElement());

		h.add(5, 1.0f);
		assertEquals(5, (int) h.minElement());

		h.add(6, 3.0f);
		assertEquals(5, (int) h.minElement());

	}

	@Test 
	public void testDecreaseKey() {

		h.add(5, 1.0f);
		h.add(6, 2.0f);
		h.add(7, 3.0f);
		h.add(8, 4.0f);

		assertEquals(5, (int) h.minElement());

		h.decreaseKey(8, 0.9f);
		assertEquals(8, (int) h.minElement());

		h.decreaseKey(7, 2.0f);
		assertEquals(8, (int) h.minElement());

		h.decreaseKey(6, 0.8f);
		assertEquals(6, (int) h.minElement());

	}




}
