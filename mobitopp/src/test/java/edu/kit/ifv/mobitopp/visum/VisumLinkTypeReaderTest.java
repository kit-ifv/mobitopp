package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper;

public class VisumLinkTypeReaderTest {

	private static final String ptWalkSpeed = "VSTD-OEVSYS(F)";
  private static final String tableName = "tableName";

	private static final int id = 0;
	private static final String name = "name";
	private static final VisumTransportSystemSet systemSet = TransportSystemHelper.dummySet();
	private static final int numberOfLanes = 1;
	private static final int capacityCar = 2;
	private static final int freeFlowSpeedCar = 3;
	private static final int walkSpeed = 4;
	private VisumTable table;
	private VisumLinkTypeReader reader;
	private NetfileAttributes attributes;

	@BeforeEach
	public void initialise() {
	  attributes = mock(NetfileAttributes.class);
		table = new VisumTable(tableName, attributes());
		reader = new VisumLinkTypeReader(table, attributes);
	}

	@Test
	public void readLinkType() {
	  when(attributes.resolve(StandardAttributes.individualWalkSpeed)).thenReturn(ptWalkSpeed);
		addLinkType();

		VisumLinkTypes linkTypes = readLinkTypes();

		assertThat(linkTypes.getById(id), is(equalTo(linkType())));
	}

	private VisumLinkType linkType() {
		return new VisumLinkType(id, name, systemSet, numberOfLanes, capacityCar, freeFlowSpeedCar,
				walkSpeed);
	}

	private void addLinkType() {
		table.addRow(linkTypeValues());
	}

	@Test
	public void readDuplicate() {
		addLinkType();
		addLinkType();

		assertThrows(IllegalArgumentException.class, () -> readLinkTypes());
	}

	private VisumLinkTypes readLinkTypes() {
		return reader.readLinkTypes(transportSystems());
	}

	private List<String> linkTypeValues() {
		return asList(asString(id), name, systemSet.serialise(), asString(numberOfLanes),
				asString(capacityCar), asString(freeFlowSpeedCar), asString(walkSpeed));
	}

	private String asString(int value) {
		return String.valueOf(value);
	}

	private VisumTransportSystems transportSystems() {
		return TransportSystemHelper.dummySystems();
	}

	private List<String> attributes() {
		return asList(VisumLinkTypeReader.id, VisumLinkTypeReader.name,
				VisumLinkTypeReader.transportSystem, VisumLinkTypeReader.numberOfLanes,
				VisumLinkTypeReader.capacityCar, VisumLinkTypeReader.freeFlowSpeed, ptWalkSpeed);
	}
}
