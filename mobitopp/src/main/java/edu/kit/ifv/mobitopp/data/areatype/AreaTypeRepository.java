package edu.kit.ifv.mobitopp.data.areatype;

public interface AreaTypeRepository {

	AreaType getTypeForCode(int code);

	AreaType getTypeForName(String name);

	AreaType getDefault();

	
}
