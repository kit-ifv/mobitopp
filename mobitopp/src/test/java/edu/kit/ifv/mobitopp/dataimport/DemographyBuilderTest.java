package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.community.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyBuilderTest {

  @Test
  public void buildsEmptyDemography() {
    DemographyData data = mock(DemographyData.class);

    DemographyBuilder builder = new DemographyBuilder(data);

    String zone = Example.someZone;
    Demography demography = builder.getDemographyFor(RegionalLevel.zone, zone);

    assertThat(demography, is(builder.createEmptyDemography(RegionalLevel.zone)));
  }
  
  @Test
	public void buildEmploymentIfExisting() {
		DemographyData data = mock(DemographyData.class);
		String zone = Example.someZone;
		when(data.hasData(RegionalLevel.zone, zone)).thenReturn(true);
		when(data.get(RegionalLevel.zone, StandardAttribute.householdSize))
				.thenReturn(Example.demographyData());

    DemographyBuilder builder = new DemographyBuilder(data);
    Demography demography = builder.getDemographyFor(RegionalLevel.zone, zone);
    
    assertThat(demography.employment(), is(EmploymentDistribution.createDefault()));
  }

}
