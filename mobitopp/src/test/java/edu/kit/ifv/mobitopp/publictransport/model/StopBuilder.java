package edu.kit.ifv.mobitopp.publictransport.model;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Collections.emptyList;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class StopBuilder {

	private final int defaultId = 0;
	private final int defaultExternalId = 0;
	private final String defaultName = "default name";
	private final Point2D defaultCoordinate = new Point2D.Float();
	private final RelativeTime defaultMinimumChangeTime = RelativeTime.of(0, MINUTES);
	private final Station defaultStation = new DefaultStation(defaultExternalId, emptyList());

	private int id;
	private String name;
	private Point2D coordinate;
	private RelativeTime minimumChangeTime;
	private Station station;
	private int externalId;

	private StopBuilder() {
		super();
		id = defaultId;
		name = defaultName;
		coordinate = defaultCoordinate;
		minimumChangeTime = defaultMinimumChangeTime;
		station = defaultStation;
		externalId = defaultExternalId;
	}

	public static StopBuilder stop() {
		return new StopBuilder();
	}

	public StopBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public Stop build() {
		return new Stop(id, name, coordinate, minimumChangeTime, station, externalId);
	}

	public StopBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public StopBuilder with(Point2D coordinate) {
		this.coordinate = coordinate;
		return this;
	}

	public StopBuilder withCoordinate(double x, double y) {
		coordinate = new Point2D.Double(x, y);
		return this;
	}
	
	public StopBuilder minimumChangeTime(RelativeTime changeTime) {
		minimumChangeTime = changeTime;
		return this;
	}
	
	public StopBuilder withStation(Station station) {
		this.station = station;
		return this;
	}

	public StopBuilder withExternalId(int externalId) {
		this.externalId = externalId;
		return this;
	}

}
