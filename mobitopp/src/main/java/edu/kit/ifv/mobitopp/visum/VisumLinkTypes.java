package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Map;

public class VisumLinkTypes implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<Integer, VisumLinkType> types;

	public VisumLinkTypes(Map<Integer, VisumLinkType> types) {
		super();
		this.types = types;
	}

	public VisumLinkType getById(int id) {
		if (types.containsKey(id)) {
			return types.get(id);
		}
		throw new IllegalArgumentException(
				"VisumLinkType: link type with id=" + id + " does not exist");
	}

}
