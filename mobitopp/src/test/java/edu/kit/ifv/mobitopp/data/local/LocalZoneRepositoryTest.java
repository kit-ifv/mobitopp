package edu.kit.ifv.mobitopp.data.local;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class LocalZoneRepositoryTest {

  private static final int zoneOid = 1;
  private static final String zoneId = "Z1";
  private static final ZoneId id = new ZoneId(zoneId, zoneOid);
  private static final int missingOid = 2;
  private Zone zone;
  private LocalZoneRepository repository;

  @BeforeEach
  public void initialise() {
    zone = mock(Zone.class);
    when(zone.getId()).thenReturn(id);
    Map<ZoneId, Zone> zones = Collections.singletonMap(id, zone);
    repository = new LocalZoneRepository(zones);
  }

  @Test
  public void findsAZone() {
    Zone zoneByOid = repository.getZoneByOid(zoneOid);

    assertThat(zoneByOid, is(sameInstance(zone)));
  }
  
  @Test
  void getByExternalId() throws Exception {
    Zone zone = repository.getByExternalId(zoneId);
    
    assertThat(zone, is(this.zone));
  }

  @Test
  public void failsForMissingZone() {
    assertThrows(IllegalArgumentException.class, () -> repository.getZoneByOid(missingOid));
  }

}
