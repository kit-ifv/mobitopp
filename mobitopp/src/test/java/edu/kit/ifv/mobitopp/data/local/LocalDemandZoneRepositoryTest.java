package edu.kit.ifv.mobitopp.data.local;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.dataimport.DemographyBuilder;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.InMemoryData;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class LocalDemandZoneRepositoryTest {

	private InMemoryData demographyData;
	private ZoneRepository zoneRepository;
	private Demography expectedDemography;
	private List<Zone> zones;
	private StructuralData zoneProperties;

	@BeforeEach
	public void initialise() throws IOException {
		RegionalLevel level = RegionalLevel.zone;
		zones = new ArrayList<>();
		zoneRepository = mock(ZoneRepository.class);
		createZones();
		demographyData = new InMemoryData();
		demographyData.store(level, StandardAttribute.householdSize, Example.demographyData());
		demographyData.store(level, StandardAttribute.maleAge, Example.demographyData());
		demographyData.store(level, StandardAttribute.femaleAge, Example.demographyData());
		demographyData.store(level, StandardAttribute.employment, Example.demographyData());
		demographyData.store(level, StandardAttribute.income, Example.demographyData());
		expectedDemography = new DemographyBuilder(demographyData).getDemographyFor(level, "1");
		zoneProperties = new StructuralData(
				CsvFile.createFrom(getClass().getResourceAsStream("ZoneProperties.csv")));
	}

	@Test
	public void oneDemandZonePerZone() {
		DemandZoneRepository demandZoneRepository = newDemandRepository();

		for (Zone zone : zoneRepository.getZones()) {
			Optional<DemandZone> demandZone = demandZoneRepository.zoneById(zone.getId());

			assertThat(demandZone.map(DemandZone::zone)).hasValueSatisfying(sameAs(zone));
		}
	}

	private <T> Condition<T> sameAs(Zone zone) {
		return new Condition<T>(z -> z == zone, "same object");
	}

	@Test
	public void useDemandFromStructuralData() {
		DemandZoneRepository demandRepository = newDemandRepository();

		Optional<DemandZone> zone = demandRepository.zoneById(new ZoneId("0" ,0));

		assertThat(zone.map(DemandZone::nominalDemography)).hasValue(expectedDemography);
	}
	
	@Test
  void limitsNumberOfZones() throws Exception {
    int numberOfZones = 2;
    DemandZoneRepository demandRepository = newDemandRepository(numberOfZones);
    
    assertThat(demandRepository.getZones()).hasSize(numberOfZones);
  }
	
	@Test
	void returnsSameInstanceRegardlessOfGetter() throws Exception {
		DemandZoneRepository demandRepository = newDemandRepository();

    RegionalContext context = new DefaultRegionalContext(RegionalLevel.zone, "1");
		DemandZone byExternalId = demandRepository.zoneByExternalId("1").get();
		DemandRegion byId = demandRepository.zoneById(byExternalId.getId()).get();
		DemandZone byContext = demandRepository.getRegionBy(context);
		assertThat(byExternalId).isSameAs(byId);
		assertThat(byExternalId).isSameAs(byContext);
	}
	
	@Test
  void failsForIncorrectContext() throws Exception {
    DemandZoneRepository demandRepository = newDemandRepository();
    
	  RegionalContext context = new DefaultRegionalContext(RegionalLevel.district, "1");
	  
	  assertThrows(IllegalArgumentException.class, () -> demandRepository.getRegionBy(context));
  }
	
	@Test
	void readsZoneProperties() throws Exception {
		DemandZoneRepository demandRepository = newDemandRepository();

		assertThat(demandRepository.zoneByExternalId("1").get().shouldGeneratePopulation()).isTrue();
		assertThat(demandRepository.zoneByExternalId("2").get().shouldGeneratePopulation()).isTrue();
		assertThat(demandRepository.zoneByExternalId("3").get().shouldGeneratePopulation()).isFalse();
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
		return LocalDemandZoneRepository.from(zoneRepository, demographyData, numberOfZones, zoneProperties);
	}
}
