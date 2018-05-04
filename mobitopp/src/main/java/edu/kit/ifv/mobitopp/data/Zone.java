package edu.kit.ifv.mobitopp.data;

import java.awt.geom.Point2D;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.NoChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public class Zone implements Serializable {

	protected static IdSequence ids = new IdSequence();

	protected final static int UNDEFINED_OID       = -1;
	protected final static byte INITIAL_VERSION = 1;

	public static final long serialVersionUID = 3459190403580397878L;
  public final static String IDPREFIX = "Z";

  private int oid = UNDEFINED_OID;
  private String id = null;
  
  // zone parameter
	private String name = null;
	private ZoneAreaType areaType = null;
	private ZoneClassificationType classification = null;

  // polygon of this zone
  private ZonePolygon polygon = null;

  private Attractivities attractivities;

  private transient DataForZone zoneData = null;
  private CarSharingDataForZone carSharingData = null;
	private ChargingDataForZone charging;

	public static void resetIdSequence() {
		ids = new IdSequence();
	}

  private Zone(int oid_)
  {
    this.oid = oid_;
  }

  public Zone(
		String id,
		String name,
		ZoneAreaType areaType,
		ZoneClassificationType classification,
		ZonePolygon polygon,
		Attractivities attractivities,
		ChargingDataForZone charging
	)
	{
		this.oid = ids.nextId();
		this.id = id;

		this.name = name;
		this.areaType = areaType;
		this.classification = classification;
		this.polygon = polygon;
		this.charging = charging;
		this.attractivities = attractivities;
	}

  public int getOid()
  {
		assert this.oid != UNDEFINED_OID;

    return this.oid;
  }

  public String getId()
  {
		assert this.id != null;
		assert !this.id.isEmpty();

    return this.id;
  }

  public ZoneAreaType getAreaType()
  {
		assert this.areaType != null;

    return this.areaType;
  }

  public ZoneClassificationType getClassification()
  {
		assert this.classification != null;

    return this.classification;
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

  public ZonePolygon getZonePolygon()
  {
		assert this.polygon != null;

    return this.polygon;
  }

	public void setCarSharing(CarSharingDataForZone carSharingData) {
		this.carSharingData = carSharingData;
	}

	public CarSharingDataForZone carSharing() {
		assert this.carSharingData != null;

		return this.carSharingData;
	}

	public Point2D centroid() {
		Point2D p = this.polygon.centroid();

		return new Point2D.Double(p.getX(), p.getY());
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

	private static Set<Integer> serializedZones = new HashSet<Integer>();
	private static Map<Integer,Zone> deSerializedZones = new HashMap<Integer,Zone>();

	private Object writeReplace() 
		throws ObjectStreamException
	{
		if (!serializedZones.contains(this.oid)) {
			serializedZones.add(this.oid);
			return this;
		} else {
			return new Zone(this.oid);
		}
	}

	private Object readResolve() 
		throws ObjectStreamException
	{
		if (!deSerializedZones.containsKey(oid)) {
			deSerializedZones.put(oid,this);
		}

		return deSerializedZones.get(oid);
	}

	@Override
	public String toString() {
		return getClass().getName() + " [oid=" + oid + ", id=" + id + ", name=" + name + "]";
	}

}
