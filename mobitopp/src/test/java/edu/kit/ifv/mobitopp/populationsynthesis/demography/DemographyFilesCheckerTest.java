package edu.kit.ifv.mobitopp.populationsynthesis.demography;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.community.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyFilesCheckerTest {

  @Test
  public void checksAvailabilityOfHouseholdSize() {
    DemographyData data = mock(DemographyData.class);
    when(data.attributes(RegionalLevel.zone)).thenReturn(asList(StandardAttribute.householdSize));
    DemographyFilesChecker checker = new DemographyFilesChecker();

    String missingAttributes = checker.calculateMissingAttributes(data);

    assertThat(missingAttributes, isEmptyString());
  }

  @Test
  public void checksAvailabilityOfDomCode() {
    DemographyData data = mock(DemographyData.class);
    when(data.attributes(RegionalLevel.community)).thenReturn(asList(StandardAttribute.domCode));
    DemographyFilesChecker checker = new DemographyFilesChecker();

    String missingAttributes = checker.calculateMissingAttributes(data);

    assertThat(missingAttributes, isEmptyString());
  }

  @Test
  public void missesAllRequiredAttributes() {
    DemographyData data = mock(DemographyData.class);
    DemographyFilesChecker checker = new DemographyFilesChecker();

    String missingAttributes = checker.calculateMissingAttributes(data);

    assertThat(missingAttributes, not(isEmptyString()));
  }
}
