package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;


import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


public class DefaultOpportunityLocationSelector implements OpportunityLocationSelector {

	static final protected Map<String,Float> landtypeWeights_DEFAULT = new LinkedHashMap<String,Float>();

	static {
			landtypeWeights_DEFAULT.put("11100",0.5f); //  Continuous Urban Fabric (S.L. > 80%)
			landtypeWeights_DEFAULT.put("11210",0.2f); // Discontinuous Dense Urban Fabric (S.L. : 50% -  80%)
			landtypeWeights_DEFAULT.put("11220",0.1f); // Discontinuous Medium Density Urban Fabric (S.L. : 30% - 50%)
			landtypeWeights_DEFAULT.put("11230",0.05f); // Discontinuous Low Density Urban Fabric (S.L. : 10% - 30%)
			landtypeWeights_DEFAULT.put("11240",0.01f); // Discontinuous Very Low Density Urban Fabric (S.L. < 10%)
			landtypeWeights_DEFAULT.put("11300",0.01f); // Isolated Structures
			landtypeWeights_DEFAULT.put("12100",1.0f); // Industrial, commercial, public, military and private units
	}

	static final protected Map<String,Float> landtypeWeights_WORK = new LinkedHashMap<String,Float>();

	static {
			landtypeWeights_WORK.put("11100",0.5f); //  Continuous Urban Fabric (S.L. > 80%)
			landtypeWeights_WORK.put("11210",0.2f); // Discontinuous Dense Urban Fabric (S.L. : 50% -  80%)
			landtypeWeights_WORK.put("12100",1.0f); // Industrial, commercial, public, military and private units
	}

	static final protected Map<String,Float> landtypeWeights_EDUCATION = new LinkedHashMap<String,Float>();

	static {
			landtypeWeights_EDUCATION.put("11100",1.0f); //  Continuous Urban Fabric (S.L. > 80%)
			landtypeWeights_EDUCATION.put("12100",0.01f); // Industrial, commercial, public, military and private units
	}

	static final protected Map<String,Float> landtypeWeights_SHOPPING = new LinkedHashMap<String,Float>();

	static {
			landtypeWeights_SHOPPING.put("11100",1.0f); //  Continuous Urban Fabric (S.L. > 80%)
			landtypeWeights_SHOPPING.put("11210",0.2f); // Discontinuous Dense Urban Fabric (S.L. : 50% -  80%)
			landtypeWeights_SHOPPING.put("12100",0.5f); // Industrial, commercial, public, military and private units
	}

	static final protected Map<String,Float> landtypeWeights_LEISURE = new LinkedHashMap<String,Float>();

	static {
			landtypeWeights_LEISURE.put("11100",1.0f); //  Continuous Urban Fabric (S.L. > 80%)
			landtypeWeights_LEISURE.put("12100",0.1f); // Industrial, commercial, public, military and private units
	}




	private final SimpleRoadNetwork network;

	private final Random random;

	public DefaultOpportunityLocationSelector(SynthesisContext context) {

		assert context != null;

		this.network = context.roadNetwork();
		this.random = new Random(context.seed());
	}



	@Override
	public Map<Location,Integer> createLocations(
		edu.kit.ifv.mobitopp.data.Zone dataZone,
		ActivityType activityType,
		Integer total_opportunities
	) {

		assert dataZone != null;
		assert this.network != null;

		Zone zone = this.network.zone(dataZone);

		Map<Location,Integer> locations = new LinkedHashMap<Location,Integer>();

		List<Integer> locationSizes = sizeOfLocations(activityType, total_opportunities);

		if (zone.isOuter() || zone.isExternal()) {

			Location location = selectZoneCenter(zone);

			locations.put(location, total_opportunities);
		} else {
			for (Integer size : locationSizes) {

				Location location = selectLocation(zone, landTypeWeights(activityType));

				locations.put(location, size);
			}
		}

		return locations;
	}


	private  Map<String,Float> landTypeWeights(ActivityType activityType) {

		switch(activityType.getTypeAsInt()) {
			case 1  : 
			case 2  : 
				return landtypeWeights_WORK;
			case 3  : 
			case 31 : 
			case 32 : 
			case 33 : 
			case 34 : 
				return landtypeWeights_EDUCATION;
			case 4  : 
			case 41 : 
			case 42 : 
				return landtypeWeights_SHOPPING;
			case 5  : 
			case 51 : 
			case 52 : 
				return landtypeWeights_LEISURE;
			case 6 : 
			case 77: 
			case 8 : 
			default:
				return landtypeWeights_DEFAULT;
		}

	}

	



	private List<Integer> sizeOfLocations(
		ActivityType activityType,
		Integer total_opportunities
	) {

		double STDDEV = 0.25;

		int number = numberOfLocations(activityType, total_opportunities);

		random.nextDouble();

		List<Double> randoms = new ArrayList<Double>();

		double running_total = 0.0;

		for (int i=0; i< number; i++) {
			double val = total_opportunities*Math.exp( STDDEV*random.nextGaussian());
			randoms.add(val);

			running_total += val;
		}

		running_total = Math.max(1.0, running_total);	

		List<Integer> sizes = new ArrayList<Integer>();

		for (int i=0; i< number; i++) {
			int val = (int) Math.round(total_opportunities*randoms.get(i)/running_total);
			sizes.add(val);
		}

		return sizes;
	}

	private Integer numberOfLocations(
		ActivityType activityType,
		Integer total_opportunities
	) {

		if (total_opportunities == 0) {
			return 1; // assures that at least one location for each activity type exists in zone
		}

		double averageSize = averageLocationSize(activityType);

		int estimatedNumber = (int) Math.round(total_opportunities/averageSize);

		return Math.max(1, estimatedNumber);
	}

	private double averageLocationSize(
		ActivityType activityType
	) {

		switch(activityType.getTypeAsInt()) {
			case 1  : 
			case 2  : 
				return 10;
			case 3  : 
			case 31 : 
				return 200;
			case 32 : 
				return 500;
			case 33 : 
				return 1000;
			case 34 : 
				return 1000;
			case 4  : 
			case 41 : 
			case 42 : 
				return 200;
			case 5  : 
			case 51 : 
			case 52 : 
				return 200;
			case 6 : 
				return 200;
			case 77: 
				return 200;
			case 8 : 
				return 200;
			default:
				return 200;
		}
	}

	protected Location selectZoneCenter(
		Zone zone
	) {

		Point2D point = zone.center();

		SimpleEdge road = zone.nearestEdge(point);

		double pos = road.nearestPositionOnEdge(point);

		return new Location(point, road.id(), pos);
	}


	protected Location selectLocation(
		Zone zone,
		Map<String,Float> landtypeWeights
	) {

		assert zone != null;

		Map<ZoneArea,Double> weighted = weightedAreaByLanduse(zone, landtypeWeights);

		ZoneArea area = new DiscreteRandomVariable<ZoneArea>(weighted).realization(random.nextDouble());

		Point2D point = area.randomPoint(random.nextInt());

		SimpleEdge road = zone.nearestEdge(point);

		double pos = road.nearestPositionOnEdge(point);

		return new Location(point, road.id(), pos);
	}

	private Map<ZoneArea,Double> weightedAreaByLanduse(Zone zone,  Map<String,Float> landtypeWeights) {

		Map<ZoneArea,Double> areasWithWeight = new LinkedHashMap<ZoneArea,Double>();

		Map<String,ZoneArea> areasByLanduse = zone.areasByLanduse(landtypeWeights.keySet()); 

		boolean empty = true;
		double total = 0.0;

		for (String landtype : landtypeWeights.keySet()) {

			ZoneArea area = areasByLanduse.get(landtype);

			if (!area.isEmpty()) {
				empty = false;
			}

			double size = area.estimateSize() * landtypeWeights.get(landtype);

			total += size;

			areasWithWeight.put(area, size);
		}

		if (empty || total == 0.0) {

			areasWithWeight = new LinkedHashMap<ZoneArea,Double>();
			areasWithWeight.put(zone.totalArea(), 1.0);
		}

		return areasWithWeight;
	}



}
