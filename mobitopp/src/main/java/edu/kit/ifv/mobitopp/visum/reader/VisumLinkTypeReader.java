package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumLinkTypes;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;

public class VisumLinkTypeReader extends VisumBaseReader {

  public VisumLinkTypeReader(NetfileLanguage language) {
    super(language);
  }

  public VisumLinkTypes readLinkTypes(VisumTransportSystems allSystems, Stream<Row> content) {
    Map<Integer, VisumLinkType> linkTypes = content
        .collect(toMap(r -> numberOf(r), r -> createLinkType(r, allSystems)));
    return new VisumLinkTypes(linkTypes);
  }

  private VisumLinkType createLinkType(Row row, VisumTransportSystems allSystems) {
    int id = numberOf(row);
    String name = nameOf(row);
    VisumTransportSystemSet systemSet = transportSystemsOf(row, allSystems);
    Integer numberOfLanes = numberOfLanesOf(row);
    Integer capacityCar = capacityCarOf(row);
    int freeFlowSpeedCar = freeFlowSpeedOf(row);
    int walkSpeed = walkSpeedOf(row);
    return new VisumLinkType(id, name, systemSet, numberOfLanes, capacityCar, freeFlowSpeedCar,
        walkSpeed);
  }

}
