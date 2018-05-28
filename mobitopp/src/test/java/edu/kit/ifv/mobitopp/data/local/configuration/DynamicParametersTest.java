package edu.kit.ifv.mobitopp.data.local.configuration;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

public class DynamicParametersTest {

	private static final String valueKey = "value";
	private static final double withinMargin = 1e-6;

	@Test
	public void valueAsInteger() {
		int expectedValue = 1;
		DynamicParameters parameters = configurationFor(expectedValue);

		int value = parameters.valueAsInteger(valueKey);

		assertThat(value, is(equalTo(expectedValue)));
	}

	@Test
	public void value() {
		String expectedValue = "attribute";
		DynamicParameters parameters = configurationFor(expectedValue);

		String value = parameters.value(valueKey);

		assertThat(value, is(equalTo(expectedValue)));
	}

	@Test
	public void valueAsDouble() {
		double expectedValue = 1.0d;
		DynamicParameters parameters = configurationFor(expectedValue);

		double value = parameters.valueAsDouble(valueKey);

		assertThat(value, is(closeTo(expectedValue, withinMargin)));
	}
	
	@Test
	public void valueAsBoolean() {
		boolean expectedValue = true;
		DynamicParameters parameters = configurationFor(expectedValue);

		boolean value = parameters.valueAsBoolean(valueKey);

		assertThat(value, is(equalTo(expectedValue)));
	}
	
	@Test
	public void valueAsFile() {
		String path = "path/to/file";
		File expected = new File(path);
		DynamicParameters parameters = configurationFor(path);
		
		File value = parameters.valueAsFile(valueKey);
		
		assertThat(value, is(equalTo(expected)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void nonExistingParameter() throws IOException {
		DynamicParameters parameters = new DynamicParameters(Collections.emptyMap());

		parameters.value("not existing");
	}

	private DynamicParameters configurationFor(Object expectedValue) {
		Map<String, String> parameters = Collections.singletonMap(valueKey,
				String.valueOf(expectedValue));
		return new DynamicParameters(parameters);
	}

}
