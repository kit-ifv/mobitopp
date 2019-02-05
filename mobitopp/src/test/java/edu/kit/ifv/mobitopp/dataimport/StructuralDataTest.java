package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StructuralDataTest {

  private final String firstZone = "1";
  private final String secondZone = "2";
  private final String thirdZone = "3";
  private StructuralData demographyData;

  @Before
  public void initialise() throws URISyntaxException {
    demographyData = Example.demographyData();
  }

  @Test
  public void getValue() {
    String first = demographyData.getValue(firstZone, "NAME");
    String second = demographyData.getValue(secondZone, "NAME");
    String third = demographyData.getValue(thirdZone, "NAME");

    assertEquals("Zone 1", first);
    assertEquals("Zone 2", second);
    assertEquals("Zone 3", third);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getValueForMissingZone() {
    String missingZone = "missingZone";
    demographyData.getValue(missingZone, "NAME");
  }

  @Test
  public void valueOrDefault() {
    int defaultValue = demographyData.valueOrDefault(firstZone, "job:infant");
    int existingValue = demographyData.valueOrDefault(secondZone, "job:infant");

    assertEquals(StructuralData.defaultValue, defaultValue);
    assertEquals(457, existingValue);
  }

  @Test
  public void valueForMissing() {
    int value = demographyData.valueOrDefault(firstZone, "missing-key");

    assertThat(value, is(StructuralData.defaultValue));
  }

  @Test
  public void missingValue() {
    boolean hasValue = demographyData.hasValue(firstZone, "missing-key");

    assertFalse(hasValue);
  }

  @Test
  public void hasValue() {
    boolean hasNameValue = demographyData.hasValue(firstZone, "NAME");
    boolean hasNoPrivateVisitValue = demographyData.hasValue(firstZone, "PrivateVisit");

    assertTrue(hasNameValue);
    assertFalse(hasNoPrivateVisitValue);
  }

  @Test
  public void getAttributes() {
    List<String> attributes = demographyData.getAttributes();

    assertThat(attributes,
        contains("id", "name", "age_m:0-5", "age_m:6-9", "age_m:10-15", "age_m:16-18",
            "age_m:19-24", "age_m:25-29", "age_m:30-44", "age_m:45-59", "age_m:60-64",
            "age_m:65-74", "age_m:75-", "age_f:0-5", "age_f:6-9", "age_f:10-15", "age_f:16-18",
            "age_f:19-24", "age_f:25-29", "age_f:30-44", "age_f:45-59", "age_f:60-64",
            "age_f:65-74", "age_f:75-", "household_size:1", "household_size:2", "household_size:3",
            "household_size:4", "household_size:5", "household_size:6", "household_size:7",
            "household_size:8", "household_size:9", "household_size:10", "household_size:11",
            "household_size:12", "job:fulltime", "job:parttime", "job:none",
            "job:education_tertiary", "job:education_secondary", "job:education_primary",
            "job:education_occup", "job:retired", "job:infant"));
  }
}
