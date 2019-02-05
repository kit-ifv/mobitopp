package edu.kit.ifv.mobitopp.data.local;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.Zone;
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

	@Before
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
			DemandZone demandZone = demandZoneRepository.zoneByOid(zone.getOid());

			assertThat(demandZone.zone(), is(sameInstance(zone)));
		}
	}

	@Test
	public void useDemandFromStructuralData() {
		DemandZoneRepository demandRepository = newDemandRepository();

		DemandZone zone = demandRepository.zoneByOid(0);

		assertThat(zone.nominalDemography(), is(equalTo(expectedDemography)));
	}

	private void createZones() {
		createZoneWithOid(0, "Z1");
		createZoneWithOid(1, "Z2");
		createZoneWithOid(2, "Z3");
	}

	private Zone createZoneWithOid(int oid, String id) {
		Zone zone = mock(Zone.class);
		when(zone.getOid()).thenReturn(oid);
		when(zone.getId()).thenReturn(id);
		zones.add(zone);
		when(zoneRepository.getZoneByOid(oid)).thenReturn(zone);
		when(zoneRepository.getZones()).thenReturn(zones);
		return zone;
	}

	private DemandZoneRepository newDemandRepository() {
		return LocalDemandZoneRepository.from(zoneRepository, demographyData);
	}
}
