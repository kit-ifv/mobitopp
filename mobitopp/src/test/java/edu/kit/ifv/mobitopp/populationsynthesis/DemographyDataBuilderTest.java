package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;

public class DemographyDataBuilderTest {

  private DataFactory dataFactory;
  private WrittenConfiguration configuration;
  private File incomeFile;
  private StructuralData incomeData;
  private File distanceFile;
  private StructuralData distanceData;

  @Before
  public void initialise() {
    dataFactory = mock(DataFactory.class);
    incomeFile = Convert.asFile("income.csv");
    incomeData = Example.demographyData();
    distanceFile = Convert.asFile("distance.csv");
    distanceData = Example.demographyData();
    configuration = new WrittenConfiguration();
  }

  @Test
  public void addDataForAllTypes() {
    Map<String, String> input = new HashMap<>();
    input.put("income", incomeFile.getName());
    input.put("distance", distanceFile.getName());
    when(dataFactory.createData(incomeFile)).thenReturn(incomeData);
    when(dataFactory.createData(distanceFile)).thenReturn(distanceData);
    configuration.setDemographyData(input);
    DemographyDataBuilder builder = newBuilder(configuration);

    DemographyData data = builder.build();

    assertThat(data.get("income"), is(sameInstance(incomeData)));
    assertThat(data.get("distance"), is(sameInstance(distanceData)));
  }

  private DemographyDataBuilder newBuilder(WrittenConfiguration configuration) {
    return new DemographyDataBuilder(configuration, dataFactory);
  }
}
