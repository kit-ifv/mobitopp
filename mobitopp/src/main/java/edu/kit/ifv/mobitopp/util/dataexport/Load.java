package edu.kit.ifv.mobitopp.util.dataexport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		throw warn(new IllegalArgumentException("Not matching OD relation"), log);
	}

}
