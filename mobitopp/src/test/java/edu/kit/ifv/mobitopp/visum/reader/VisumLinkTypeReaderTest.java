package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumLinkTypes;
import edu.kit.ifv.mobitopp.visum.VisumTable;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.reader.VisumLinkTypeReader;

public class VisumLinkTypeReaderTest {

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
  private NetfileLanguage language;

  @BeforeEach
  public void initialise() {
		language = StandardNetfileLanguages.defaultSystems().german();
    table = new VisumTable(tableName, attributes());
    reader = new VisumLinkTypeReader(language);
  }

  @Test
  public void readLinkType() {
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

    assertThrows(IllegalStateException.class, () -> readLinkTypes());
  }

  private VisumLinkTypes readLinkTypes() {
    return reader.readLinkTypes(transportSystems(), table.rows());
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
    return asList(StandardAttributes.number, StandardAttributes.name,
            StandardAttributes.transportSystemSet, StandardAttributes.numberOfLanes,
            StandardAttributes.capacityCar, StandardAttributes.freeFlowSpeedCar,
            StandardAttributes.publicTransportWalkSpeed)
        .stream()
        .map(language::resolve)
        .collect(toList());
  }
}
