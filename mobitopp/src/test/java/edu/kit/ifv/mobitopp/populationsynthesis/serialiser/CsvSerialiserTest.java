package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvSerialiserTest {

	protected static final String[] prepared = new String[0];
	private static final String[] header = new String[] { "header" };

	private CSVWriter writer;
	private SerialiserFormat<Object> format;
	private Object original = new Object();

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		writer = mock(CSVWriter.class);
		format = mock(SerialiserFormat.class);
	}
	
	@Test
	public void serialiseHeader() {
		when(format.header()).thenReturn(asList(header));
		
		serialiser().writeHeader();
		
		verify(writer).writeNext(header);
	}
	
	@Test
	public void serialisePreparedData() {
		when(format.prepare(original)).thenReturn(asList(prepared));

		CsvSerialiser<Object> serialiser = serialiser();
		serialiser.write(original);
		
		verify(writer).writeNext(prepared);
	}
	
	@Test
	public void closesWriter() throws Exception {
		serialiser().close();
		
		verify(writer).close();
	}

	private CsvSerialiser<Object> serialiser() {
		return new CsvSerialiser<Object>(writer, format);
	}
}
