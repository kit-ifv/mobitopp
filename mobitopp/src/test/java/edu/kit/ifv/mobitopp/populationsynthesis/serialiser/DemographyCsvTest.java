package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyCsvTest {

  private DemographyCsv demographyCsv;

  @Before
  public void initialise() {
    ExampleDemandZones zones = ExampleDemandZones.create();
    DemandZoneRepository repository = mock(DemandZoneRepository.class);
    when(repository.getZones()).thenReturn(zones.asList());
    List<AttributeType> attributeTypes = asList(StandardAttribute.householdSize,
        StandardAttribute.maleAge, StandardAttribute.femaleAge);
    demographyCsv = new DemographyCsv(attributeTypes, repository);
  }

  @Test
  public void createsHeader() {
    String header = demographyCsv.createHeader().stream().collect(joining(";"));

    assertEquals(
        "externalId;matrixColumn;household_size:1-1;household_size:2-2;age_m:0-10;age_m:11-2147483647;age_f:0-5;age_f:6-2147483647",
        header);
  }

  @Test
  public void serialiseActual() throws IOException {
    @SuppressWarnings("unchecked")
    Consumer<String> results = mock(Consumer.class);
    demographyCsv.serialiseActual(results);

    String content = "1;1;0;0;0;0;0;0" + System.lineSeparator() + "2;2;0;0;0;0;0;0" + System.lineSeparator();
    verify(results).accept(content);
  }

  @Test
  public void serialiseNominal() throws IOException {
    @SuppressWarnings("unchecked")
    Consumer<String> results = mock(Consumer.class);
    demographyCsv.serialiseNominal(results);

    String content = "1;1;2;3;4;2;2;1" + System.lineSeparator() + "2;2;2;3;4;2;2;1" + System.lineSeparator();
    verify(results).accept(content);
  }
}
