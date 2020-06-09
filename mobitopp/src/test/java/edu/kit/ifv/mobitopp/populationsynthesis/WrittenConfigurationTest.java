package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DataSource;

public class WrittenConfigurationTest {

  @Test
  void copiesExistingConfiguration() {
    WrittenConfiguration original = new WrittenConfiguration();
    original.setCarOwnership(new CarOwnership());
    original.setCommuterTicket("commutingTicket");
    original.setDataSource(mock(DataSource.class));
    original.setDemandRegionMapping(Map.of("test", "test"));
    original.setDemographyData(Map.of("test", "test"));
    original.setMaxGoodnessDelta(1.0d);
    original.setMaxIterations(1);
    original.setPanelData("panelData");
    original.setSeed(1);
    original.setSynthesisZoneProperties("some-properties.csv");
    original.setVisumFile("visumFile");

    WrittenConfiguration copied = new WrittenConfiguration(original);

    assertAll(
        () -> assertValue(WrittenConfiguration::getActivityScheduleAssigner, copied, original),
        () -> assertValue(WrittenConfiguration::getCarOwnership, copied, original),
        () -> assertValue(WrittenConfiguration::getMobilityProviders, copied, original),
        () -> assertValue(WrittenConfiguration::getCommuterTicket, copied, original),
        () -> assertValue(WrittenConfiguration::getDataSource, copied, original),
        () -> assertValue(WrittenConfiguration::getDemographyData, copied, original),
        () -> assertValue(WrittenConfiguration::getExperimental, copied, original),
        () -> assertValue(WrittenConfiguration::getMaxGoodnessDelta, copied, original),
        () -> assertValue(WrittenConfiguration::getMaxIterations, copied, original),
        () -> assertValue(WrittenConfiguration::getNumberOfZones, copied, original),
        () -> assertValue(WrittenConfiguration::getPanelData, copied, original),
        () -> assertValue(WrittenConfiguration::getResultFolder, copied, original),
        () -> assertValue(WrittenConfiguration::getSeed, copied, original),
        () -> assertValue(WrittenConfiguration::getSynthesisZoneProperties, copied, original),
        () -> assertValue(WrittenConfiguration::getVisumFile, copied, original),
        () -> assertValue(WrittenConfiguration::getVisumToMobitopp, copied, original));
  }

}
