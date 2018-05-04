package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCustomerModel;
import edu.kit.ifv.mobitopp.simulation.carsharing.LogitBasedCarSharingCustomerModel;

public class CarSharingParser {

	private final Map<String, String> models;
	private final Random random;

	public CarSharingParser(Map<String, String> models, long seed) {
		super();
		random = new Random(seed);
		this.models = models;
	}

	public Map<String, CarSharingCustomerModel> parse() {
		Map<String, CarSharingCustomerModel> customerModels = new TreeMap<>();
		for (Entry<String, String> entry : models.entrySet()) {
			customerModels.put(entry.getKey(), modelFor(entry));
		}
		return customerModels;
	}

	private LogitBasedCarSharingCustomerModel modelFor(Entry<String, String> entry) {
		String company = entry.getKey();
		String parameterFile = entry.getValue();
		return new LogitBasedCarSharingCustomerModel(random, company, parameterFile);
	}

}
