package edu.kit.ifv.mobitopp.result;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CsvBuilderTest {

	private CsvBuilder builder;
	private String delimiter;

	@Before
	public void initialise() {
		delimiter = ";";
		builder = new CsvBuilder(delimiter);
	}

	@Test
	public void appendInteger() {
		int value = 5;

		builder.append(value);

		verifyDelimiter(value);
	}

	@Test
	public void appendLong() {
		long value = 5l;

		builder.append(value);

		verifyDelimiter(value);
	}

	@Test
	public void appendFloat() {
		float value = 5.0f;

		builder.append(value);

		verifyDelimiter(value);
	}

	@Test
	public void appendDouble() {
		double value = 5.0d;

		builder.append(value);

		verifyDelimiter(value);
	}

	@Test
	public void appendBoolean() {
		boolean value = true;

		builder.append(value);

		verifyDelimiter(value);
	}

	@Test
	public void appendString() {
		String value = "test";

		builder.append(value);

		verifyDelimiter(value);
	}

	@Test
	public void appendObject() {
		Object value = new Object();

		builder.append(value);

		verifyDelimiter(value);
	}

	private void verifyDelimiter(Object value) {
		String csv = builder.toString();
		assertEquals(value + delimiter, csv);
	}

	@Test
	public void appendWithoutDelimiter() {
		String value = "test";

		builder.appendWithoutDelimiter(value);

		String csv = builder.toString();
		assertEquals(value, csv);
	}

	@Test
	public void newLineFloat() {
		float value = 5.0f;

		builder.newLine(value);

		verifyNewLine(value);
	}

	@Test
	public void newLineString() {
		String value = "test";

		builder.newLine(value);

		verifyNewLine(value);
	}

	private void verifyNewLine(Object value) {
		String csv = builder.toString();
		assertEquals(value + System.lineSeparator(), csv);
	}
}
