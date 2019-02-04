package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class AttractivitiesBuilder {

	private final AttractivitiesData data;
	private final Attractivities attractivities;

	public AttractivitiesBuilder(AttractivitiesData data) {
		super();
		this.data = data;
		this.attractivities = new Attractivities();
	}

	public Attractivities attractivities(String zoneId) {
		add(ActivityType.WORK, zoneId, "Work");
		add(ActivityType.BUSINESS, zoneId, "Business");
		add(ActivityType.EDUCATION, zoneId, "Education");
		add(ActivityType.SHOPPING, zoneId, "Shopping");
		add(ActivityType.LEISURE, zoneId, "Leisure");
		add(ActivityType.SERVICE, zoneId, "Service");
		addExisting(ActivityType.EDUCATION_PRIMARY, zoneId, "Education_Primary");
		addExisting(ActivityType.EDUCATION_SECONDARY, zoneId, "Education_Secondary");
		addExisting(ActivityType.EDUCATION_TERTIARY, zoneId, "Education_Tertiary");
		addExisting(ActivityType.EDUCATION_OCCUP, zoneId, "Education_Occup");
		addExisting(ActivityType.LEISURE_INDOOR, zoneId, "Leisure_Indoor");
		addExisting(ActivityType.LEISURE_OUTDOOR, zoneId, "Leisure_Outdoor");
		addExisting(ActivityType.LEISURE_OTHER, zoneId, "Leisure_Other");
		addExisting(ActivityType.SHOPPING_DAILY, zoneId, "Shopping_Daily");
		addExisting(ActivityType.SHOPPING_OTHER, zoneId, "Shopping_Other");
		addExisting(ActivityType.PRIVATE_BUSINESS, zoneId, "Private_Business");
		addExisting(ActivityType.PRIVATE_VISIT, zoneId, "Private_Visit");
		addExisting(ActivityType.OTHERHOME, zoneId, "Otherhome");
		return attractivities;
	}

	private void addExisting(ActivityType activityType, String zoneId, String key) {
		if (data.hasValue(zoneId, prefixed(key))) {
			add(activityType, zoneId, key);
		}
	}

	private String prefixed(String key) {
		return "Attractivity:" + key;
	}

	private void add(ActivityType type, String zoneId, String key) {
		int value = data.valueOrDefault(zoneId, prefixed(key));
		attractivities.addAttractivity(type, value);
	}

}
