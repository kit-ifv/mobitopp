package edu.kit.ifv.mobitopp.util.dataimport;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;


public class Bbsr17Repository implements AreaTypeRepository {

	@Override
	public AreaType getTypeForCode(int code) {
		for (Bbsr17 type : Bbsr17.values()) {
			if (type.getTypeAsInt() == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				String.format("No BBSR17 area type for %s code available.", code));
	}

	@Override
	public AreaType getTypeForName(String name) {
		for (Bbsr17 type : Bbsr17.values()) {
			if (type.getTypeAsString().equals(name)) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				String.format("No BBSR17 area type for %s code available.", name));
	}

	@Override
	public AreaType getDefault() {
		return Bbsr17.defaultType;
	}

}
