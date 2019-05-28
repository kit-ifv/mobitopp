package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumLinkTypeReader {

  private final NetfileLanguage language;

  public VisumLinkTypeReader(NetfileLanguage language) {
    super();
    this.language = language;
  }

  public VisumLinkTypes readLinkTypes(VisumTransportSystems allSystems, Stream<Row> content) {
    Map<Integer, VisumLinkType> linkTypes = content
        .collect(toMap(r -> idIn(r), r -> createLinkType(r, allSystems)));
    return new VisumLinkTypes(linkTypes);
  }

  private VisumLinkType createLinkType(Row row, VisumTransportSystems allSystems) {
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

  private int idIn(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.number));
  }

  private String attribute(StandardAttributes attribute) {
    return language.resolve(attribute);
  }

  private String nameIn(Row row) {
    return row.get(attribute(StandardAttributes.name));
  }

  private VisumTransportSystemSet transportSystemsIn(Row row, VisumTransportSystems allSystems) {
    String transportSystems = row.get(attribute(StandardAttributes.transportSystemSet));
    return VisumTransportSystemSet.getByCode(transportSystems, allSystems);
  }

  private int numberOfLanesIn(Row row) {
    return getIntegerIn(row, attribute(StandardAttributes.numberOfLanes));
  }

  private int capacityOfCarIn(Row row) {
    return getIntegerIn(row, attribute(StandardAttributes.capacityCar));
  }

  private Integer getIntegerIn(Row row, String attribute) {
    return row.valueAsInteger(attribute);
  }

  private int freeFlowSpeedIn(Row row) {
    return parseSpeed(row.get(attribute(StandardAttributes.freeFlowSpeedCar)));
  }

  private int parseSpeed(String value) {
    return VisumNetworkReader.parseSpeed(value, language);
  }

  private int walkSpeed(Row row) {
    return VisumNetworkReader.walkSpeed(row, language);
  }

}
