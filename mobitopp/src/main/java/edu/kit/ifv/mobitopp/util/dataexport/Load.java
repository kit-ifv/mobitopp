package edu.kit.ifv.mobitopp.util.dataexport;

public class Load {

	private final OdRelation odRelation;
	private int load;

	public Load(OdRelation odRelation) {
		super();
		this.odRelation = odRelation;
	}

	public Load(OdRelation odRelation, int load) {
		super();
		this.odRelation = odRelation;
		this.load = load;
	}

	public OdRelation odRelation() {
		return odRelation;
	}

	public Load add(Load other) {
		if (odRelation.equals(other.odRelation())) {
			return new Load(odRelation, load + other.load);
		}
		throw new IllegalArgumentException("Not matching OD relation");
	}

}
