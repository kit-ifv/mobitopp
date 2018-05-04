package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VisumTransportSystemReaderTest {

	private static final String code = "code-1";
	private static final String name = "name-1";
	private static final String type = "typ-1";
	private static final String tableName = "tableName";
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private VisumTable table;
	private VisumTransportSystemReader reader;

	@Before
	public void initialise() {
		table = new VisumTable(tableName, VisumTransportSystemReader.attributes());
		reader = new VisumTransportSystemReader(table);
	}

	@Test
	public void readEmptySystems() {
		VisumTransportSystems systems = readTransportSystems();

		assertTrue(systems.isEmpty());
	}

	@Test
	public void readSystems() {
		addTransportSystem();

		VisumTransportSystems systems = readTransportSystems();

		VisumTransportSystem expected = new VisumTransportSystem(code, name, type);
		assertThat(systems.getBy(code), is(equalTo(expected)));
	}

	private void addTransportSystem() {
		table.addRow(asList(code, name, type));
	}

	@Test
	public void readDuplicateSystems() {
		addTransportSystem();
		addTransportSystem();

		thrown.expect(IllegalArgumentException.class);
		readTransportSystems();
	}

	private VisumTransportSystems readTransportSystems() {
		return reader.readTransportSystems();
	}

}
