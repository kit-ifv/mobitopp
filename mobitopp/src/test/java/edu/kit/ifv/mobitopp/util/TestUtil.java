package edu.kit.ifv.mobitopp.util;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

public abstract class TestUtil {

	public static <T> void assertValue(Function<T, ?> value, T actual, T expected) {
		assertThat(value.apply(actual), is(equalTo(value.apply(expected))));
	}
}
