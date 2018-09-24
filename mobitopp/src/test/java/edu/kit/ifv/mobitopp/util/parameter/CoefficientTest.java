package edu.kit.ifv.mobitopp.util.parameter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import edu.kit.ifv.mobitopp.util.parameter.Coefficient;

public class CoefficientTest {

	private static final double margin = 1e-6;

	@Test
	public void valueAsDouble() {
		double doubleValue = 2.5;
		Coefficient coefficient = newCoefficientFor(doubleValue);

		double asDouble = coefficient.asDouble();

		assertThat(asDouble, is(closeTo(doubleValue, margin)));
	}

	@Test
	public void valueAsInteger() {
		int intValue = 2;
		Coefficient coefficient = newCoefficientFor(intValue);

		int asInt = coefficient.asInt();

		assertThat(asInt, is(intValue));
	}

	private Coefficient newCoefficientFor(Object value) {
		String name = "name";
		return new Coefficient(name, value.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ensuresDoubleValueIsNotInfinity() {
		newCoefficientFor(Double.POSITIVE_INFINITY).asDouble();
	}

	@Test(expected = IllegalArgumentException.class)
	public void ensuresDoubleValueIsANumber() {
		newCoefficientFor(Double.NaN).asDouble();
	}
	
}
