package edu.kit.ifv.mobitopp.dataimport;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Value;
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

    assertThat(insideArea).isEqualTo(ZoneClassificationType.studyArea);
    assertThat(insideExtendedArea).isEqualTo(ZoneClassificationType.extendedStudyArea);
    assertThat(outsideArea).isEqualTo(ZoneClassificationType.outlyingArea);
  }

  @Test
  public void zoneAreaTypeFromString() {
    AreaType first = data.currentZoneAreaType(Example.someZone);
    AreaType second = data.currentZoneAreaType(Example.anotherZone);
    AreaType third = data.currentZoneAreaType(Example.otherZone);

    assertThat(first).isEqualTo(ZoneAreaType.CITYOUTSKIRT);
    assertThat(second).isEqualTo(ZoneAreaType.CONURBATION);
    assertThat(third).isEqualTo(ZoneAreaType.DEFAULT);
  }

  @Test
  public void zoneAreaTypeFromCode() {
    ZonePropertiesData attractivityData = new ZonePropertiesData(Example.attractivityDataByCode(),
        Example.areaTypeRepository());
    AreaType first = attractivityData.currentZoneAreaType(Example.someZone);
    AreaType second = attractivityData.currentZoneAreaType(Example.anotherZone);
    AreaType third = attractivityData.currentZoneAreaType(Example.otherZone);

    assertThat(first).isEqualTo(ZoneAreaType.CITYOUTSKIRT);
    assertThat(second).isEqualTo(ZoneAreaType.CONURBATION);
    assertThat(third).isEqualTo(ZoneAreaType.DEFAULT);
  }
  
  @Test
  void getValues() throws Exception {
    Map<String, Value> values = data.getValues(Example.someZone);
    
    assertThat(values).isEqualTo(data.data().getValues(Example.someZone));
  }
}
