package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyCsvTest {

  private DemographyCsv demographyCsv;

  @BeforeEach
  public void initialise() {
    ExampleDemandZones zones = ExampleDemandZones.create();
    List<AttributeType> attributeTypes = asList(StandardAttribute.householdSize,
        StandardAttribute.maleAge, StandardAttribute.femaleAge);
    demographyCsv = new DemographyCsv(attributeTypes, zones::asList);
  }

  @Test
  public void createsHeader() {
    String header = demographyCsv.createHeader().stream().collect(joining(";"));

    assertEquals(
        "externalId;household_size:1-1;household_size:2-2;age_m:0-10;age_m:11-2147483647;age_f:0-5;age_f:6-2147483647",
        header);
  }
  
  @Test
	void createsHeaderForEmptyDemography() throws Exception {
  	List<AttributeType> attributeTypes = asList(StandardAttribute.householdSize,
        StandardAttribute.maleAge, StandardAttribute.femaleAge);
  	DemographyCsv demographyCsv = new DemographyCsv(attributeTypes, List::of);
  	
  	String header = demographyCsv.createHeader().stream().collect(joining(";"));
  	
  	assertThat(header).isEmpty();
	}

  @Test
  public void serialiseActual() throws IOException {
    @SuppressWarnings("unchecked")
    Consumer<String> results = mock(Consumer.class);
    demographyCsv.serialiseActual(results);

		String content = new StringBuilder()
				.append("1;0;0;0;0;0;0")
				.append(System.lineSeparator())
				.append("2;0;0;0;0;0;0")
				.append(System.lineSeparator())
				.append("3;0;0;0;0;0;0")
				.append(System.lineSeparator())
				.toString();
    verify(results).accept(content);
  }

  @Test
  public void serialiseNominal() throws IOException {
    @SuppressWarnings("unchecked")
    Consumer<String> results = mock(Consumer.class);
    demographyCsv.serialiseNominal(results);

		String content = new StringBuilder()
				.append("1;2;3;4;2;2;1")
				.append(System.lineSeparator())
				.append("2;2;3;4;2;2;1")
				.append(System.lineSeparator())
				.append("3;2;3;4;2;2;1")
				.append(System.lineSeparator())
				.toString();
    verify(results).accept(content);
  }
}
