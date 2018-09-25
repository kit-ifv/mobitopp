package edu.kit.ifv.mobitopp.util.dataimport;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;

public enum Bbsr17 implements AreaType {
	defaultType(0, "default"),
	largerCentralCitiesInAgglomerationAreas(1, "Larger central cities in agglomeration areas"),
	centralCitiesInAgglomerationAreas(2, "Central cities in agglomeration areas"),
	highOrderCentresInHighlyAgglomeratedCountiesInAgglomerationAreas(3, "High-order centres in highly agglomerated counties in agglomeration areas"),
	highlyAgglomeratedCountiesInAgglomerationAreas(4, "Highly agglomerated counties in agglomeration areas"),
	highOrderCentresInAgglomeratedCountiesInAgglomerationAreas(5, "High-order centres in agglomerated counties in agglomeration areas"),
	agglomeratedCountiesInAgglomerationAreas(6, "Agglomerated counties in agglomeration areas"),
	highOrderCentresInRuralCountiesInAgglomerationAreas(7, "High-order centres in rural Counties in agglomeration areas"),
	ruralCountiesInAgglomerationAreas(8, "Rural counties in agglomeration areas"),
	centralCitiesInUrbanizedAreas(9, "Central cities in urbanized areas"),
	highOrderCentresInAgglomeratedCountiesInUrbanizedAreas(10, "High-order centres in agglomerated Counties in urbanized areas"),
	agglomeratedCountiesInUrbanizedAreas(11, "Agglomerated counties in urbanized areas"),
	highOrderCentresInRuralCountiesInUrbanizedAreas(12, "High-order centres in rural Counties in urbanized areas"),
	ruralCountiesInUrbanizedAreas(13, "Rural Counties in urbanized areas"),
	highOrderCentresInRuralCountiesWithHigherDensityInRuralAreas(14, "High-order centres in rural counties with higher density in rural areas"),
	ruralCountiesWithHigherDensityInRuralAreas(15, "Rural counties with higher density in rural areas"),
	highOrderCentresInRuralCountiesWithLowerDensityInRuralAreas(16, "High-order centres in rural counties with lower density in rural areas"),
	ruralCountiesWithLowerDensityInRuralAreas(17, "Rural counties with lower density in rural areas")
	;

	private final int code;
	private final String name;

	private Bbsr17(int code, String name) {
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
