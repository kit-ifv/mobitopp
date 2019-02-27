package edu.kit.ifv.mobitopp.visum;

import java.util.HashMap;
import java.util.Map;

public class VisumLinkTypeReader {

	private final VisumTable table;
	private final Map<Integer, VisumLinkType> linkTypes;
  private final NetfileAttributes attributes;

	public VisumLinkTypeReader(VisumTable table, NetfileAttributes attributes) {
		super();
		this.table = table;
    this.attributes = attributes;
		linkTypes = new HashMap<>();
	}

	public VisumLinkTypes readLinkTypes(VisumTransportSystems allSystems) {
		for (int row = 0; row < table.numberOfRows(); row++) {
			createIn(row, allSystems);
		}
		return new VisumLinkTypes(linkTypes);
	}

	private void createIn(int row, VisumTransportSystems allSystems) {
		verifyLinkTypeIn(row);
		VisumLinkType linkType = createLinkType(row, allSystems);
		store(row, linkType);
	}

	private void verifyLinkTypeIn(int row) {
		if (linkTypes.containsKey(idIn(row))) {
			throw new IllegalArgumentException(
					"VisumLinkType: link type with id=" + idIn(row) + " already exists");
		}
	}

	private void store(int row, VisumLinkType linkType) {
		linkTypes.put(idIn(row), linkType);
	}

	private VisumLinkType createLinkType(int row, VisumTransportSystems allSystems) {
		int id = idIn(row);
		String name = nameIn(row);
		VisumTransportSystemSet systemSet = transportSystemsIn(row, allSystems);
		Integer numberOfLanes = numberOfLanesIn(row);
		Integer capacityCar = capacityOfCarIn(row);
		int freeFlowSpeedCar = freeFlowSpeedIn(row);
		int walkSpeed = walkSpeed(row);
		return new VisumLinkType(id, name, systemSet, numberOfLanes, capacityCar, freeFlowSpeedCar,
				walkSpeed);
	}

	private int idIn(int row) {
		return getIntegerIn(row, attribute(StandardAttributes.number));
	}

  private String attribute(StandardAttributes attribute) {
    return attributes.resolve(attribute);
  }

	private String nameIn(int row) {
		return table.getValue(row, attribute(StandardAttributes.name));
	}

	private VisumTransportSystemSet transportSystemsIn(int row, VisumTransportSystems allSystems) {
		String transportSystems = table.getValue(row, attribute(StandardAttributes.transportSystemSet));
		return VisumTransportSystemSet.getByCode(transportSystems, allSystems);
	}

	private int numberOfLanesIn(int row) {
		return getIntegerIn(row, attribute(StandardAttributes.numberOfLanes));
	}

	private int capacityOfCarIn(int row) {
		return getIntegerIn(row, attribute(StandardAttributes.capacityCar));
	}

	private Integer getIntegerIn(int row, String attribute) {
		return Integer.parseInt(table.getValue(row, attribute));
	}

	private int freeFlowSpeedIn(int row) {
		return parseSpeed(table.getValue(row, attribute(StandardAttributes.freeFlowSpeedCar)));
	}

	private int parseSpeed(String value) {
		return VisumNetworkReader.parseSpeed(value);
	}

	private int walkSpeed(int row) {
		return VisumNetworkReader.walkSpeed(table, row, attributes);
	}

}
