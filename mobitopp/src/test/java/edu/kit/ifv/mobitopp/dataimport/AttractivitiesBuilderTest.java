package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class AttractivitiesBuilderTest {

  private AttractivitiesData attractivityData;

  @Before
  public void initialise() {
    attractivityData = new AttractivitiesData(Example.attractivityData(),
        Example.areaTypeRepository());
  }

  @Test
  public void parseAttractivities() {
    String zoneId = Example.someZone;
    
    Attractivities attractivities = new AttractivitiesBuilder(attractivityData).attractivities(zoneId);

    assertThat(attractivities, is(equalTo(expectedAttractivities())));
  }

  private Attractivities expectedAttractivities() {
    Attractivities attractivities = new Attractivities();
    attractivities.addAttractivity(ActivityType.WORK, 2745);
    attractivities.addAttractivity(ActivityType.BUSINESS, 2745);
    attractivities.addAttractivity(ActivityType.EDUCATION, 181);
    attractivities.addAttractivity(ActivityType.SHOPPING, 1906);
    attractivities.addAttractivity(ActivityType.LEISURE, 778);
    attractivities.addAttractivity(ActivityType.SERVICE, 433);
    attractivities.addAttractivity(ActivityType.EDUCATION_PRIMARY, 181);
    attractivities.addAttractivity(ActivityType.EDUCATION_SECONDARY, 0);
    attractivities.addAttractivity(ActivityType.EDUCATION_TERTIARY, 0);
    attractivities.addAttractivity(ActivityType.EDUCATION_OCCUP, 0);
    attractivities.addAttractivity(ActivityType.LEISURE_INDOOR, 238);
    attractivities.addAttractivity(ActivityType.LEISURE_OUTDOOR, 395);
    attractivities.addAttractivity(ActivityType.LEISURE_OTHER, 778);
    attractivities.addAttractivity(ActivityType.SHOPPING_DAILY, 1000);
    attractivities.addAttractivity(ActivityType.SHOPPING_OTHER, 13);
    attractivities.addAttractivity(ActivityType.PRIVATE_BUSINESS, 893);
    attractivities.addAttractivity(ActivityType.OTHERHOME, 2745);
    return attractivities;
  }
}
