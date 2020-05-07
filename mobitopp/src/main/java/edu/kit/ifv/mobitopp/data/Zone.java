package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.NoChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public class Zone {

	private final ZoneId id;
	private final ZoneProperties zoneProperties;
	private final Attractivities attractivities;
	private final ChargingDataForZone charging;

  private transient DataForZone zoneData;
  private CarSharingDataForZone carSharingData;
	private MaasDataForZone maasData;

	public Zone(
			final ZoneId id, final ZoneProperties zoneProperties, final Attractivities attractivities,
			final ChargingDataForZone charging) {
		super();
		this.id = id;
		this.zoneProperties = zoneProperties;
		this.attractivities = attractivities;
		this.charging = charging;
	}
	
	public ZoneId getId() {
		return id;
	}

	public AreaType getAreaType() {
		return zoneProperties.getAreaType();
	}
  
  public RegionType getRegionType() {
    return zoneProperties.getRegionType();
  }

	public ZoneClassificationType getClassification() {
		return zoneProperties.getClassification();
	}

	public int getNumberOfParkingPlaces() {
		return zoneProperties.getParkingPlaces();
	}

	public String getName() {
		return zoneProperties.getName();
	}

  public Location centroidLocation() {
  	return zoneProperties.getCentroidLocation();
  }
  
	public boolean isDestination() {
		return zoneProperties.isDestination();
	}
	
	public boolean isDestinationFor(ActivityType activityType) {
		return isDestination() && opportunities().locationsAvailable(activityType);
	}
	
	public double getRelief() {
		return zoneProperties.getRelief();
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

	@Override
  public String toString() {
    return "Zone [internalId=" + id + ", name=" + getName() + "]";
  }

}
