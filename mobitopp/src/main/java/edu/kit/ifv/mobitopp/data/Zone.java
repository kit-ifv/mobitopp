package edu.kit.ifv.mobitopp.data;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.NoChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public class Zone implements Serializable {

	protected final static int UNDEFINED_OID       = -1;
	protected final static byte INITIAL_VERSION = 1;

	public static final long serialVersionUID = 3459190403580397878L;
  public final static String IDPREFIX = "Z";

  private final ZoneId internalId;
	private String name = null;
	private AreaType areaType;
	private RegionType regionType;
	private ZoneClassificationType classification = null;
	private int parkingPlaces;
  private Location centroidLocation;
  private Attractivities attractivities;

  private transient DataForZone zoneData = null;
  private CarSharingDataForZone carSharingData = null;
	private ChargingDataForZone charging;
	private MaasDataForZone maasData;

  private Zone(ZoneId internalId) {
    super();
    this.internalId = internalId;
  }

  public Zone(
      ZoneId id, String name, AreaType areaType, RegionType regionType,
      ZoneClassificationType classification, int parkingPlaces, Location centroidLocation,
      Attractivities attractivities, ChargingDataForZone charging) {
    this(id);
    this.name = name;
    this.areaType = areaType;
    this.regionType = regionType;
    this.classification = classification;
    this.parkingPlaces = parkingPlaces;
    this.charging = charging;
    this.attractivities = attractivities;
    this.centroidLocation = centroidLocation;
  }

  public int getOid() {
    return internalId.getMatrixColumn();
  }

  public String getId() {
    return this.internalId.getExternalId();
  }

  public AreaType getAreaType()
  {
		assert this.areaType != null;

    return this.areaType;
  }
  
  public RegionType getRegionType() {
    assert null != this.regionType;
    return regionType;
  }

  public ZoneClassificationType getClassification()
  {
		assert this.classification != null;

    return this.classification;
  }

  public int getNumberOfParkingPlaces() {
    return parkingPlaces;
  }

	public DataForZone getDemandData() 
	{
		if (this.zoneData == null) {
			this.zoneData = new DataForZone(this, attractivities);
		}

		return this.zoneData;
	}

  public boolean hasDemandData()
  {
		return this.zoneData != null;
  }

  public String getName()
  {
		assert  this.name != null;

    return this.name;
  }

  public Location centroidLocation() {
  	return this.centroidLocation;
  }

	public void setCarSharing(CarSharingDataForZone carSharingData) {
		this.carSharingData = carSharingData;
	}

	public CarSharingDataForZone carSharing() {
		assert this.carSharingData != null;

		return this.carSharingData;
	}
	
	public void setMaas(MaasDataForZone maasData) {
	  this.maasData = maasData;
	}

  public MaasDataForZone maas() {
    assert this.maasData != null;
    return maasData;
  }

	public OpportunityDataForZone opportunities() {
		return getDemandData().opportunities();
	}

	public Attractivities attractivities() {
		return opportunities().attractivities();
	}

	public float getAttractivity(ActivityType activityType) {
		Attractivities attractivity = attractivities();

		float opportunity = 1.0f;

		if (attractivity.getItems().containsKey(activityType)) {
			opportunity = attractivity.getItems().get(activityType);
		}

		return opportunity;
	}

	public int numberOfChargingPoints() {
		return charging().numberOfChargingPoints();
	}

	public ChargingDataForZone charging() {
		return null == charging ? NoChargingDataForZone.noCharging : charging;
	}

	// (de)serialization

	private static Set<ZoneId> serializedZones = new HashSet<>();
	private static Map<ZoneId,Zone> deSerializedZones = new HashMap<>();

  private Object writeReplace() throws ObjectStreamException {
    if (!serializedZones.contains(this.internalId)) {
      serializedZones.add(this.internalId);
      return this;
    } else {
      return new Zone(this.internalId);
    }
  }

  private Object readResolve() throws ObjectStreamException {
    if (!deSerializedZones.containsKey(internalId)) {
      deSerializedZones.put(internalId, this);
    }

    return deSerializedZones.get(internalId);
  }

	@Override
  public String toString() {
    return "Zone [internalId=" + internalId + ", name=" + name + "]";
  }

}
