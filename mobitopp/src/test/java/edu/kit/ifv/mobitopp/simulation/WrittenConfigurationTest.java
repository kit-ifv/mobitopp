package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;

import org.junit.Test;

public class WrittenConfigurationTest {

	@Test
	public void copies() {
		WrittenConfiguration original = new WrittenConfiguration();
		
		WrittenConfiguration copied = new WrittenConfiguration(original);
		
		assertValue(WrittenConfiguration::getFractionOfPopulation, copied, original);
		assertValue(WrittenConfiguration::getSeed, copied, original);
		assertValue(WrittenConfiguration::getDays, copied, original);
		assertValue(WrittenConfiguration::getNumberOfZones, copied, original);
		assertValue(WrittenConfiguration::getDataSource, copied, original);
		assertValue(WrittenConfiguration::getPublicTransport, copied, original);
		assertValue(WrittenConfiguration::getResultFolder, copied, original);
		assertValue(WrittenConfiguration::getVisumFile, copied, original);
		assertValue(WrittenConfiguration::getDestinationChoice, copied, original);
		assertValue(WrittenConfiguration::getModeChoice, copied, original);
		assertValue(WrittenConfiguration::getTimeStepLength, copied, original);
		assertValue(WrittenConfiguration::getVisumToMobitopp, copied, original);
		assertValue(WrittenConfiguration::getExperimental, copied, original);
		assertValue(WrittenConfiguration::getLogLevel, copied, original);
		assertValue(WrittenConfiguration::getThreadCount, copied, original);
		assertValue(WrittenConfiguration::getPort, copied, original);
		
	}
}
