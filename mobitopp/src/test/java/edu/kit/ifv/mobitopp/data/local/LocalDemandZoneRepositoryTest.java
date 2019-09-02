package edu.kit.ifv.mobitopp.data.local;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.dataimport.DemographyBuilder;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.populationsynthesis.InMemoryData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class LocalDemandZoneRepositoryTest {

	private InMemoryData demographyData;
	private ZoneRepository zoneRepository;
	private Demography expectedDemography;
	private List<Zone> zones;

	@BeforeEach
	public void initialise() {
		zones = new ArrayList<>();
		zoneRepository = mock(ZoneRepository.class);
		createZones();
		demographyData = new InMemoryData();
		demographyData.store(StandardAttribute.householdSize, Example.demographyData());
		demographyData.store(StandardAttribute.maleAge, Example.demographyData());
		demographyData.store(StandardAttribute.femaleAge, Example.demographyData());
		demographyData.store(StandardAttribute.employment, Example.demographyData());
		demographyData.store(StandardAttribute.income, Example.demographyData());
		expectedDemography = new DemographyBuilder(demographyData).build("1");
	}

	@Test
	public void oneDemandZonePerZone() {
		DemandZoneRepository demandZoneRepository = newDemandRepository();

		for (Zone zone : zoneRepository.getZones()) {
			Optional<DemandZone> demandZone = demandZoneRepository.zoneById(zone.getId());

			assertThat(demandZone.map(DemandZone::zone), hasValue(sameInstance(zone)));
		}
	}

	@Test
	public void useDemandFromStructuralData() {
		DemandZoneRepository demandRepository = newDemandRepository();

		Optional<DemandZone> zone = demandRepository.zoneById(new ZoneId("0" ,0));

		assertThat(zone.map(DemandZone::nominalDemography), hasValue(equalTo(expectedDemography)));
	}
	
	@Test
  void limitsNumberOfZones() throws Exception {
    int numberOfZones = 2;
    DemandZoneRepository demandRepository = newDemandRepository(numberOfZones);
    
    assertThat(demandRepository.getZones(), hasSize(numberOfZones));
  }

	private void createZones() {
		createZoneWithOid(0, "1");
		createZoneWithOid(1, "2");
		createZoneWithOid(2, "3");
	}

	private Zone createZoneWithOid(int oid, String id) {
		Zone zone = mock(Zone.class);
		when(zone.getId()).thenReturn(new ZoneId(id, oid));
		ZoneId zoneId = new ZoneId(id, oid);
    when(zone.getId()).thenReturn(zoneId);
		zones.add(zone);
		when(zoneRepository.getZoneById(zoneId)).thenReturn(zone);
		when(zoneRepository.getZones()).thenReturn(zones);
		return zone;
	}
	
	private DemandZoneRepository newDemandRepository() {
	  return newDemandRepository(Integer.MAX_VALUE);
	}

	private DemandZoneRepository newDemandRepository(int numberOfZones) {
		return LocalDemandZoneRepository.from(zoneRepository, demographyData, numberOfZones);
	}
}
