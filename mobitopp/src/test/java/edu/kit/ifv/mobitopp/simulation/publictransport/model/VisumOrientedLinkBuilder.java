package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper.asSet;
import static edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper.dummySet;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;

import edu.kit.ifv.mobitopp.visum.VisumLinkAttributes;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

public class VisumOrientedLinkBuilder {

	private static final String defaultId = "default id";
	private static final VisumNode defaultFrom = visumNode().build();
	private static final VisumNode defaultTo = visumNode().build();
	private static final String defaultName = "default name";
	private static final VisumLinkType defaultLinkType;
	private static final VisumTransportSystemSet defaultSystemSet = dummySet();
	private static final float defaultLength = 0;
	private static final int defaultCapacityCar = 0;
	private static final int defaultFreeFlowSpeedCar = 0;
	private static final int defaultNumberOfLanes = 0;
	private static final int defaultWalkSpeed = 0;

	static {
		defaultLinkType = new VisumLinkType(0, "default name", defaultSystemSet, 0, 0, 0, 0);
	}

	private String id;
	private VisumNode from;
	private VisumNode to;
	private String name;
	private VisumLinkType linkType;
	private VisumTransportSystemSet transportSystemSet;
	private float length;
	private int capacityCar;
	private int freeFlowSpeedCar;
	private int walkSpeed;
  private int numberOfLanes;

	public VisumOrientedLinkBuilder() {
		super();
		id = defaultId;
		from = defaultFrom;
		to = defaultTo;
		name = defaultName;
		linkType = defaultLinkType;
		transportSystemSet = defaultSystemSet;
		length = defaultLength;
		capacityCar = defaultCapacityCar;
		freeFlowSpeedCar = defaultFreeFlowSpeedCar;
		numberOfLanes = defaultNumberOfLanes;
		walkSpeed = defaultWalkSpeed;
	}

	public VisumOrientedLink build() {
		VisumLinkAttributes attributes = new VisumLinkAttributes(numberOfLanes, capacityCar, freeFlowSpeedCar, walkSpeed);
		return new VisumOrientedLink(id, from, to, name, linkType, transportSystemSet, length, attributes);
	}

	public VisumOrientedLinkBuilder withId(String id) {
		this.id = id;
		return this;
	}

	public VisumOrientedLinkBuilder from(VisumNode from) {
		this.from = from;
		return this;
	}

	public VisumOrientedLinkBuilder to(VisumNode to) {
		this.to = to;
		return this;
	}

	public VisumOrientedLinkBuilder with(VisumLinkType type) {
		linkType = type;
		return this;
	}

	public VisumOrientedLinkBuilder withWalkSpeed(int speed) {
		this.walkSpeed = speed;
		return this;
	}
	
	public VisumOrientedLinkBuilder withCarCapacity(int capacityCar) {
		this.capacityCar = capacityCar;
		return this;
	}
	
	public VisumOrientedLinkBuilder withFreeFlowSpeedCar(int speed) {
		this.freeFlowSpeedCar = speed;
		return this;
	}

	public VisumOrientedLinkBuilder withLength(float length) {
		this.length = length;
		return this;
	}

	public VisumOrientedLinkBuilder with(VisumTransportSystem transportSystem) {
		return with(asSet(transportSystem));
	}
	
	public VisumOrientedLinkBuilder with(VisumTransportSystemSet transportSystemSet) {
	  this.transportSystemSet = transportSystemSet;
	  return this;
	}

  public VisumOrientedLinkBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public VisumOrientedLinkBuilder withNumberOfLanes(int numberOfLanes) {
    this.numberOfLanes = numberOfLanes;
    return this;
  }

}
