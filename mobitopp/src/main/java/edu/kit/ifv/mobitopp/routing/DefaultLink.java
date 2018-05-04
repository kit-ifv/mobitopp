package edu.kit.ifv.mobitopp.routing;

class DefaultLink 
	implements Link
{

	private final Node from;
	private final Node to;

	private final float distance;
	private final float travelTime;

	private final String id;

	public DefaultLink(
		String id,
		DefaultNode from, 
		DefaultNode to, 
		float distance, 
		float travelTime
	) {
		assert from != null;
		assert to != null;
		
		this.id=id;
		this.from=from;
		this.to=to;

		this.distance = distance;
		this.travelTime = travelTime;
	}

	public Node from() {
		assert this.from != null;

		return this.from;
	}

	public Node to() {
		assert this.to != null;

		return this.to;
	}

	public String id() {
		return this.id;
	}

	public float distance() {
		return this.distance;
	}

	public float travelTime() {
		return this.travelTime;
	}


	public String toString() {
		return "(" + from + "," + to + ": " + distance() + ")";
	}

}
