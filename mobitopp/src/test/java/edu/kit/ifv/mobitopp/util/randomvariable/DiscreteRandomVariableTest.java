package edu.kit.ifv.mobitopp.util.randomvariable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

public class DiscreteRandomVariableTest {

	SortedMap<Integer,Double> data;
	SortedMap<Integer,Double> data2;

	DiscreteRandomVariable<Integer> randomVariable;

	Integer FIRST  = 1;
	Integer SECOND = 2;
	Integer THIRD  = 3;
	Integer FOURTH = 4;


	@Before
	public void setUp() {

		data = new TreeMap<Integer,Double>();

		data.put(FIRST,  0.25);
		data.put(SECOND, 0.25);
		data.put(THIRD,  0.25);
		data.put(FOURTH, 0.25);

		randomVariable = new DiscreteRandomVariable<Integer>(data);

		data2 = new TreeMap<Integer,Double>();
		data2.put(FIRST,  1.25);
		data2.put(SECOND, 1.25);
		data2.put(THIRD,  1.25);
		data2.put(FOURTH, 1.25);
	}

	@Test
	public void testRealization() {

		assertEquals(FIRST,  randomVariable.realization(0.1));
		assertEquals(SECOND, randomVariable.realization(0.3));
		assertEquals(THIRD,  randomVariable.realization(0.6));
		assertEquals(FOURTH, randomVariable.realization(0.8));
	}

	@Test
	public void testReproducibility() {

		assertEquals(FIRST,  randomVariable.realization(0.1));
		assertEquals(FIRST,  randomVariable.realization(0.1));
		assertEquals(FIRST,  randomVariable.realization(0.1));
		assertEquals(FIRST,  randomVariable.realization(0.1));
		assertEquals(FIRST,  randomVariable.realization(0.1));
	}

	@Test
	public void testCornerCases() {

		assertTrue(0.25-Math.ulp(0.25) < 0.25);

		assertEquals(FIRST,  randomVariable.realization(0.0));
		assertEquals(FIRST,  randomVariable.realization(0.25-Math.ulp(0.25)));
		assertEquals(SECOND, randomVariable.realization(0.25));
		assertEquals(SECOND, randomVariable.realization(0.5-Math.ulp(0.5)));
		assertEquals(THIRD,  randomVariable.realization(0.5));
		assertEquals(THIRD,  randomVariable.realization(0.75-Math.ulp(0.75)));
		assertEquals(FOURTH, randomVariable.realization(0.75));
		assertEquals(FOURTH, randomVariable.realization(1.0));
	}

	@Test
	public void testZeroProbability() {

		TreeMap<Integer,Double> mydata = new TreeMap<Integer,Double>();

		mydata.put(FIRST,  0.5);
		mydata.put(SECOND, 0.0);
		mydata.put(THIRD,  0.5);


		DiscreteRandomVariable<Integer> rv = new DiscreteRandomVariable<Integer>(mydata);

		assertEquals(FIRST,  rv.realization(0.1));
		assertEquals(FIRST,  rv.realization(0.49));
		assertEquals(FIRST,  rv.realization(0.5-Math.ulp(0.5)));
		assertEquals(THIRD,  rv.realization(0.5));
		assertEquals(THIRD,  rv.realization(0.9));
	}

}
