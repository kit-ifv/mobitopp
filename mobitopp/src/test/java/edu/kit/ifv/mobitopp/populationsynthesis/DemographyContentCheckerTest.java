package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyContentCheckerTest {

  private DemographyData data;
  private DemographyContentChecker checker;

  @Before
  public void initialise() {
    data = mock(DemographyData.class);
    checker = new DemographyContentChecker();
  }

  @Test
  public void checkDemographyContainsAttributesOfType() {
    when(data.attributes()).thenReturn(asList(StandardAttribute.femaleAge));
    when(data.get(StandardAttribute.femaleAge)).thenReturn(Example.demographyData());

    checker.verify(data);
  }

  @Test(expected = IllegalArgumentException.class)
  public void failsForMissingAttributeType() {
    when(data.attributes()).thenReturn(asList(StandardAttribute.distance));
    when(data.get(StandardAttribute.distance)).thenReturn(Example.demographyData());

    checker.verify(data);
  }

  @Test
  public void calculateMissingAttributes() {
    when(data.attributes())
        .thenReturn(asList(StandardAttribute.distance, StandardAttribute.femaleAge));
    StructuralData structuralData = Example.demographyData();
    when(data.get(StandardAttribute.distance)).thenReturn(structuralData);
    when(data.get(StandardAttribute.femaleAge)).thenReturn(structuralData);

    List<AttributeType> missingAttributes = checker.calculateMissingAttributes(data);

    assertThat(missingAttributes, hasItem(StandardAttribute.distance));
  }

}
