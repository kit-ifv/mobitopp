package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Value;

public class ColumnMappingTest {

	@Test
	public void getValueForField() {
		ColumnMapping<Object> columns = new ColumnMapping<>();

		columns.add("field1", Function.identity());
		columns.add("field2", Function.identity());

		Value firstValue = new Value("first-value");
		Value secondValue = new Value("second-value");
		List<String> fromData = asList(firstValue.toString(), secondValue.toString());
		Value firstField = columns.get("field1", fromData);
		Value secondField = columns.get("field2", fromData);

		assertThat(firstField, is(equalTo(firstValue)));
		assertThat(secondField, is(equalTo(secondValue)));
	}

	@Test
	void considersOffset() throws Exception {
		int offset = 1;
		ColumnMapping<Object> columns = new ColumnMapping<>(offset);

		columns.add("field2", Function.identity());

		Value secondValue = new Value("second-value");
		List<String> fromData = asList("first-value", secondValue.toString());
		Value secondField = columns.get("field2", fromData);

		assertThat(secondField, is(equalTo(secondValue)));
	}

	@Test
	void cacheHeader() throws Exception {
		ColumnMapping<Object> columns = new ColumnMapping<>();

		columns.add("field1", Function.identity());
		columns.add("field2", Function.identity());

		List<String> firstCall = columns.header();
		List<String> secondCall = columns.header();

		assertThat(firstCall, is(sameInstance(secondCall)));
	}

	@Test
	void refreshHeaderOnColumnChange() throws Exception {
		ColumnMapping<Object> columns = new ColumnMapping<>();

		columns.add("field1", Function.identity());
		List<String> firstCall = columns.header();

		columns.add("field2", Function.identity());
		List<String> secondCall = columns.header();

		assertAll(() -> assertThat(firstCall, contains("field1")),
				() -> assertThat(secondCall, contains("field1", "field2")));
	}

	@Test
	void indexOf() throws Exception {
		ColumnMapping<Object> columns = new ColumnMapping<>();
		columns.add("field1", Function.identity());
		columns.add("field2", Function.identity());

		assertAll(() -> assertThat(columns.indexOf("field1"), is(equalTo(0))),
				() -> assertThat(columns.indexOf("field2"), is(equalTo(1))));
	}

	@Test
	void convertsValuesToString() throws Exception {
		ColumnMapping<Dummy> columns = new ColumnMapping<>();
		columns.add("field1", d -> d.field);
		columns.add("field2", d -> d.field2);
		int value1 = 0;
		String value2 = "value2";
		Dummy dummy = new Dummy(value1, asList(value2));
		
		List<String> prepared = columns.prepare(dummy);
		
		assertThat(prepared, contains(String.valueOf(value1), value2));
	}

	private final static class Dummy {

		private final int field;
		private final List<String> field2;

		private Dummy(int field, List<String> field2) {
			this.field = field;
			this.field2 = field2;

		}
	}
}
