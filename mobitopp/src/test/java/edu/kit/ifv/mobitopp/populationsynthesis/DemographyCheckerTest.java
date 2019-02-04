package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DemographyCheckerTest {

  @Test
  public void allZonesAreSane() {
    List<Integer> zoneIds = asList(1, 2);
    DemographyData data = mock(DemographyData.class);
    when(data.hasData(anyString())).thenReturn(true);
    List<String> sink = new ArrayList<>();
    DemographyChecker checker = new DemographyChecker(zoneIds, sink::add);

    checker.check(data);

    assertThat(sink, is(empty()));

    zoneIds.stream().map(String::valueOf).forEach(id -> verify(data).hasData(id));
  }

  @Test
  public void checksMissingZone() {
    List<Integer> zoneIds = asList(1, 2);
    DemographyData data = mock(DemographyData.class);
    when(data.hasData("1")).thenReturn(true);
    List<String> sink = new ArrayList<>();
    DemographyChecker checker = new DemographyChecker(zoneIds, sink::add);

    checker.check(data);

    assertThat(sink, is(equalTo(asList("2"))));

    zoneIds.stream().map(String::valueOf).forEach(id -> verify(data).hasData(id));
  }
}
