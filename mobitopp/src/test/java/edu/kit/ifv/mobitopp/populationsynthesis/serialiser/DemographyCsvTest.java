package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;

public class DemographyCsvTest {

  @Test
  public void createsHeader() {
    ExampleDemandZones zones = ExampleDemandZones.create();
    DemandZoneRepository repository = mock(DemandZoneRepository.class);
    when(repository.getZones()).thenReturn(zones.asList());
    DemographyCsv demographyCsv = new DemographyCsv(repository);

    String header = demographyCsv.createHeader().stream().collect(joining(";"));

    assertEquals("1;HHTyp:1;HHTyp:2;Age:M:0-10;Age:M:11-2147483647;Age:F:0-5;Age:F:6-2147483647",
        header);
  }

  @Test
  public void serialiseActual() throws IOException {
    ExampleDemandZones zones = ExampleDemandZones.create();
    DemandZoneRepository repository = mock(DemandZoneRepository.class);
    when(repository.getZones()).thenReturn(zones.asList());
    DemographyCsv demographyCsv = new DemographyCsv(repository);
    @SuppressWarnings("unchecked")
    Consumer<String> results = mock(Consumer.class);
    demographyCsv.serialiseActual(results);

    String content = "1;0;0;0;0;0;0\r\n" + "2;0;0;0;0;0;0\r\n";
    verify(results).accept(content);
  }

  @Test
  public void serialiseNominal() throws IOException {
    ExampleDemandZones zones = ExampleDemandZones.create();
    DemandZoneRepository repository = mock(DemandZoneRepository.class);
    when(repository.getZones()).thenReturn(zones.asList());
    DemographyCsv demographyCsv = new DemographyCsv(repository);
    @SuppressWarnings("unchecked")
    Consumer<String> results = mock(Consumer.class);
    demographyCsv.serialiseNominal(results);

    String content = "1;2;3;4;2;2;1\r\n" + "2;2;3;4;2;2;1\r\n";
    verify(results).accept(content);
  }
}
