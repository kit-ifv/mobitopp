package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Category;

public class CarSharingCategories {

	public final Category carsharing = carsharing();

	private Category carsharing() {
		List<String> header = new ArrayList<>();
		return new Category("carsharing", header);
	}

}
