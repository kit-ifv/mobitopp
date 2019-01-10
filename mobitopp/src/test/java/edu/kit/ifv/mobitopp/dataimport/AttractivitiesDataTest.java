package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;

public class AttractivitiesDataTest {

  private AttractivitiesData attractivityData;

  @Before
  public void initialise() throws URISyntaxException {
    attractivityData = new AttractivitiesData(Example.attractivityData(),
        Example.areaTypeRepository());
  }

  @Test
  public void classification() {
    ZoneClassificationType insideArea = attractivityData.currentClassification();
    attractivityData.next();
    ZoneClassificationType outsideArea = attractivityData.currentClassification();

    assertThat(insideArea, is(equalTo(ZoneClassificationType.areaOfInvestigation)));
    assertThat(outsideArea, is(equalTo(ZoneClassificationType.outlyingArea)));
  }

  @Test
  public void zoneAreaTypeFromString() {
    AreaType first = attractivityData.currentZoneAreaType();
    attractivityData.next();
    AreaType second = attractivityData.currentZoneAreaType();
    attractivityData.next();
    AreaType third = attractivityData.currentZoneAreaType();

    assertThat(first, is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
    assertThat(second, is(equalTo(ZoneAreaType.CONURBATION)));
    assertThat(third, is(equalTo(ZoneAreaType.DEFAULT)));
  }

  @Test
  public void zoneAreaTypeFromCode() {
    AttractivitiesData attractivityData = new AttractivitiesData(Example.attractivityDataByCode(),
        Example.areaTypeRepository());
    AreaType first = attractivityData.currentZoneAreaType();
    attractivityData.next();
    AreaType second = attractivityData.currentZoneAreaType();
    attractivityData.next();
    AreaType third = attractivityData.currentZoneAreaType();

    assertThat(first, is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
    assertThat(second, is(equalTo(ZoneAreaType.CONURBATION)));
    assertThat(third, is(equalTo(ZoneAreaType.DEFAULT)));
  }
}
