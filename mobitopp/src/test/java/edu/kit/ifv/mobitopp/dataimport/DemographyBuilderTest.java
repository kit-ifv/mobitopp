package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;

public class DemographyBuilderTest {

  @Test
  public void buildsEmptyFemaleAge() {
    DemographyData data = mock(DemographyData.class);

    DemographyBuilder builder = new DemographyBuilder(data);

    String zone = Example.someZone;
    Demography demography = builder.build(zone);

    assertThat(demography, is(DemographyBuilder.emptyDemography()));

  }

}
