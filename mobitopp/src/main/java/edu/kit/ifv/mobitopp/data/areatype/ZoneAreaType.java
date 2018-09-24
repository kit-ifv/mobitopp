package edu.kit.ifv.mobitopp.data.areatype;

public enum ZoneAreaType implements AreaType {

	DEFAULT(0, "DEFAULT"),
	RURAL(1, "RURAL"),
	PROVINCIAL(2, "PROVINCIAL"),
	CITYOUTSKIRT(3, "CITYOUTSKIRT"),
	METROPOLITAN(4, "METROPOLITAN"),
	CONURBATION(5, "CONURBATION");

	private final int code;
	private final String name;

	private ZoneAreaType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public int getTypeAsInt() {
		return code;
	}

	@Override
	public String getTypeAsString() {
		return name;
	}

}
