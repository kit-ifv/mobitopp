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

	public Attractivities attractivities() {
		add(ActivityType.WORK, "Work");
		add(ActivityType.BUSINESS, "Business");
		add(ActivityType.EDUCATION, "Education");
		add(ActivityType.SHOPPING, "Shopping");
		add(ActivityType.LEISURE, "Leisure");
		add(ActivityType.SERVICE, "Service");
		addExisting(ActivityType.EDUCATION_PRIMARY, "Education_Primary");
		addExisting(ActivityType.EDUCATION_SECONDARY, "Education_Secondary");
		addExisting(ActivityType.EDUCATION_TERTIARY, "Education_Tertiary");
		addExisting(ActivityType.EDUCATION_OCCUP, "Education_Occup");
		addExisting(ActivityType.LEISURE_INDOOR, "Leisure_Indoor");
		addExisting(ActivityType.LEISURE_OUTDOOR, "Leisure_Outdoor");
		addExisting(ActivityType.LEISURE_OTHER, "Leisure_Other");
		addExisting(ActivityType.SHOPPING_DAILY, "Shopping_Daily");
		addExisting(ActivityType.SHOPPING_OTHER, "Shopping_Other");
		addExisting(ActivityType.PRIVATE_BUSINESS, "Private_Business");
		addExisting(ActivityType.PRIVATE_VISIT, "Private_Visit");
		addExisting(ActivityType.OTHERHOME, "Otherhome");
		return attractivities;
	}

	private void addExisting(ActivityType activityType, String key) {
		if (data.hasValue(prefixed(key))) {
			add(activityType, key);
		}
	}

	private String prefixed(String key) {
		return "Attractivity:" + key;
	}

	private void add(ActivityType type, String key) {
		int value = data.valueOrDefault(prefixed(key));
		attractivities.addAttractivity(type, value);
	}

}
