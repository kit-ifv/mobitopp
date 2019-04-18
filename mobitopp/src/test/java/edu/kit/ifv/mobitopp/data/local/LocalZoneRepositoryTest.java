package edu.kit.ifv.mobitopp.data.local;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;

public class LocalZoneRepositoryTest {

  private static final int zoneOid = 1;
  private static final String zoneId = "Z1";
  private static final int missingOid = 2;
  private Zone zone;
  private LocalZoneRepository repository;

  @BeforeEach
  public void initialise() {
    zone = mock(Zone.class);
    when(zone.getId()).thenReturn(zoneId);
    when(zone.getOid()).thenReturn(zoneOid);
    Map<Integer, Zone> zones = Collections.singletonMap(zoneOid, zone);
    repository = new LocalZoneRepository(zones);
  }

  @Test
  public void findsAZone() {
    Zone zoneByOid = repository.getZoneByOid(zoneOid);

    assertThat(zoneByOid, is(sameInstance(zone)));
  }

  @Test
  void resolvesIdToOid() throws Exception {
    int oid = repository.map(zone.getId());

    assertThat(oid, is(equalTo(zoneOid)));
  }

  @Test
  public void failsForMissingZone() {
    assertThrows(IllegalArgumentException.class, () -> repository.getZoneByOid(missingOid));
  }

}
