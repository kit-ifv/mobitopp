package edu.kit.ifv.mobitopp.dataimport;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class BikeSharingPropertiesDataTest {

	private static final String propertiesData = "zoneId;\"operating_area:BikeRent\";\"number_of_bikes:BikeRent\""
			+ System.lineSeparator() + "1;1;2";

	@Test
	void buildCompaniesOnce() throws Exception {
		ZoneId zoneId = new ZoneId("1", 0);
		BikeSharingPropertiesData data = new BikeSharingPropertiesData(properties());

		Map<String, BikeSharingCompany> companies = data.buildCompanies(zoneId);
		Map<String, BikeSharingCompany> secondCall = data.buildCompanies(zoneId);

		assertThat(companies).containsKey("BikeRent");
		assertThat(companies.values()).isSameAs(secondCall.values());
	}
	
	@Test
	void buildServiceAreaForZone() throws Exception {
		ZoneId zoneId = new ZoneId("1", 0);
		BikeSharingPropertiesData data = new BikeSharingPropertiesData(properties());

		Map<String, Boolean> serviceArea = data.buildServiceAreaFor(zoneId);
		
		assertThat(serviceArea).containsEntry("BikeRent", true);
	}

	private StructuralData properties() throws IOException {
		return new StructuralData(
				CsvFile.createFrom(new ByteArrayInputStream(propertiesData.getBytes())));
	}
}
