package edu.kit.ifv.mobitopp.dataimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;

@ExtendWith(MockitoExtension.class)
public class BikeSharingPropertiesDataTest {

	private static final String companyName = "BikeRent";

	private static final String propertiesData = "zoneId;\"operating_area:" + companyName + "\";\"number_of_bikes:" + companyName + "\""
			+ System.lineSeparator() + "1;1;2"
			+ System.lineSeparator() + "2;1;3";

	@Mock
	private Person person;
	@Mock(lenient = true)
	private IdToOidMapper idMapper;

	private ZoneId someZone;

	private ZoneId otherZone;
	
	@BeforeEach
	public void beforeEach() {
		someZone = new ZoneId("1", 0);
		otherZone = new ZoneId("2", 1);
		when(idMapper.mapToZoneId(someZone.getExternalId())).thenReturn(someZone);
		when(idMapper.mapToZoneId(otherZone.getExternalId())).thenReturn(otherZone);
	}

	@Test
	void buildCompaniesOnce() throws Exception {
		BikeSharingPropertiesData data = new BikeSharingPropertiesData(properties(), idMapper);

		Map<String, BikeSharingCompany> companies = data.getCompanies();
		Map<String, BikeSharingCompany> secondCall = data.getCompanies();

		assertThat(companies).containsKey(companyName);
		assertThat(companies.values()).isSameAs(secondCall.values());
	}
	
	@Test
	void buildCompaniesInAllZones() throws Exception {
		when(person.isMobilityProviderCustomer(companyName)).thenReturn(true);
		BikeSharingPropertiesData data = new BikeSharingPropertiesData(properties(), idMapper);

		BikeSharingDataForZone someData = data.getData(someZone);
		BikeSharingDataForZone otherData = data.getData(otherZone);

		assertThat(someData.isBikeAvailableFor(person)).isTrue();
		assertThat(otherData.isBikeAvailableFor(person)).isTrue();
	}
	
	@Test
	void buildServiceAreaForZone() throws Exception {
		ZoneId zoneId = new ZoneId("1", 0);
		BikeSharingPropertiesData data = new BikeSharingPropertiesData(properties(), idMapper);

		Map<String, Boolean> serviceArea = data.buildServiceAreaFor(zoneId);
		
		assertThat(serviceArea).containsEntry(companyName, true);
	}

	private StructuralData properties() throws IOException {
		return new StructuralData(
				CsvFile.createFrom(new ByteArrayInputStream(propertiesData.getBytes())));
	}
}
