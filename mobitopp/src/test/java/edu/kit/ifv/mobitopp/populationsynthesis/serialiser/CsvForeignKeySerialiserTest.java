package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvForeignKeySerialiserTest {

	private static final String[] prepared = new String[0];
	private static final String[] header = new String[] { "header" };
	private static final Object value = new Object();

	private CSVWriter writer;
	private ForeignKeySerialiserFormat<Object> format;
	private PopulationContext context;
	
	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		context = mock(PopulationContext.class);
		writer = mock(CSVWriter.class);
		format = mock(ForeignKeySerialiserFormat.class);
	}
	
	@Test
	public void serialiseHeader() {
		when(format.header()).thenReturn(asList(header));
		
		serialiser().writeHeader();
		
		verify(writer).writeNext(header);
	}

	@Test
	public void serialisePreparedData() {
		when(format.prepare(value, context)).thenReturn(asList(prepared));

		ForeignKeySerialiser<Object> serialiser = serialiser();
		serialiser.write(value, context);

		verify(writer).writeNext(prepared);
	}
	
	@Test
	public void closesWriter() throws Exception {
		serialiser().close();
		
		verify(writer).close();
	}

	private ForeignKeySerialiser<Object> serialiser() {
		return new CsvForeignKeySerialiser<>(writer, format);
	}
}
