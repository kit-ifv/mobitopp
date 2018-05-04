package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Category;

public class DemandCategories {

	public final Category demanddataCalculation = data();
	public final Category demanddataCalculationHouseholdSelection = demanddataCalculation;
	public final Category demanddataOpportunities = opportunities();
	public final Category demanddataResult = result();
	public final Category demanddataResultPerson = person();
	public final Category demanddataResultHousehold = household();
	public final Category demanddataResultCar = car();

	private Category data() {
		List<String> header = new ArrayList<>();
		return new Category("demanddata", header);
	}

	private Category opportunities() {
		List<String> header = new ArrayList<>();
		return new Category("demanddataOpportunities", header);
	}

	private Category result() {
		List<String> header = new ArrayList<>();
		return new Category("demanddataResult", header);
	}

	private Category person() {
		List<String> header = new ArrayList<>();
		return new Category("demanddataResult_Person", header);
	}

	private Category household() {
		List<String> header = new ArrayList<>();
		return new Category("demanddataResult_Household", header);
	}

	private Category car() {
		List<String> header = new ArrayList<>();
		return new Category("demanddataResult_Car", header);
	}
}
