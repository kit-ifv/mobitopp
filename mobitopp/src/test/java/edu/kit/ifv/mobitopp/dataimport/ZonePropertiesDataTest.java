package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;

public class ZonePropertiesDataTest {

  private ZonePropertiesData data;

  @BeforeEach
  public void initialise() throws URISyntaxException {
    data = new ZonePropertiesData(Example.attractivityData(), Example.areaTypeRepository());
  }
  

  @Test
  public void classification() {
    ZoneClassificationType insideArea = data.currentClassification(Example.someZone);
    ZoneClassificationType insideExtendedArea = data.currentClassification(Example.anotherZone);
    ZoneClassificationType outsideArea = data.currentClassification(Example.otherZone);

    assertThat(insideArea, is(equalTo(ZoneClassificationType.studyArea)));
    assertThat(insideExtendedArea, is(equalTo(ZoneClassificationType.extendedStudyArea)));
    assertThat(outsideArea, is(equalTo(ZoneClassificationType.outlyingArea)));
  }

  @Test
  public void zoneAreaTypeFromString() {
    AreaType first = data.currentZoneAreaType(Example.someZone);
    AreaType second = data.currentZoneAreaType(Example.anotherZone);
    AreaType third = data.currentZoneAreaType(Example.otherZone);

    assertThat(first, is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
    assertThat(second, is(equalTo(ZoneAreaType.CONURBATION)));
    assertThat(third, is(equalTo(ZoneAreaType.DEFAULT)));
  }

  @Test
  public void zoneAreaTypeFromCode() {
    ZonePropertiesData attractivityData = new ZonePropertiesData(Example.attractivityDataByCode(),
        Example.areaTypeRepository());
    AreaType first = attractivityData.currentZoneAreaType(Example.someZone);
    AreaType second = attractivityData.currentZoneAreaType(Example.anotherZone);
    AreaType third = attractivityData.currentZoneAreaType(Example.otherZone);

    assertThat(first, is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
    assertThat(second, is(equalTo(ZoneAreaType.CONURBATION)));
    assertThat(third, is(equalTo(ZoneAreaType.DEFAULT)));
  }
}
