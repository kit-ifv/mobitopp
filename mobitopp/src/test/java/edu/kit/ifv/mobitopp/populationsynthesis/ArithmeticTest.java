package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.junit.Assert.*;

import org.junit.Test;


public class ArithmeticTest {

	@Test
	public void test() {
		assertEquals(0.0d, 1/2, 1e-6);
		assertEquals(0.5d, 1.0d/2, 1e-6);
		assertEquals(0.5d, 1/2.0d, 1e-6);
		assertEquals(0.5d, 1.0d/2.0d, 1e-6);
		assertEquals(0.5d, 1l/2.0d, 1e-6);
		assertEquals(0.5d, 1.0f/2.0d, 1e-6);
	}

}
