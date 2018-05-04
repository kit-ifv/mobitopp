package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

public class CsvDeserialiserTest {

	private static final String[] endOfFile = null;
	private static final String[] serialisedValue = new String[0];
	private static final Object parsedValue = new Object();
	
	private CSVReader reader;
	private SerialiserFormat<Object> format;
	
	@SuppressWarnings("unchecked")
	@Before
	public void initialise() throws IOException {
		reader = mock(CSVReader.class);
		format = mock(SerialiserFormat.class);
		when(format.parse(asList(serialisedValue))).thenReturn(parsedValue);
		when(reader.readNext()).thenReturn(serialisedValue, endOfFile);
	}

	@Test
	public void deserialisePreparedData() throws Exception {
		try (Deserialiser<Object> deserialiser = deserialiser()) {
			List<Object> deserialisedElements = deserialiser.deserialise();

			assertThat(deserialisedElements, contains(parsedValue));
		}
	}

	private CsvDeserialiser<Object> deserialiser() {
		return new CsvDeserialiser<>(reader, format);
	}
	
	@Test
	public void closesReader() throws Exception {
		deserialiser().close();
		
		verify(reader).close();
	}
}
