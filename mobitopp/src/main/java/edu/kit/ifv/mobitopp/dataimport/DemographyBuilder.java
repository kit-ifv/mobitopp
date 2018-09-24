package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;

public class DemographyBuilder {

	private final StructuralData structuralData;

	public DemographyBuilder(StructuralData structuralData) {
		super();
		this.structuralData = structuralData;
	}

	public Demography build() {
		HouseholdDistribution hhDistribution = parseHouseholdDistribution();
		MaleAgeDistribution maleDistribution = parseMaleDistribution();
		FemaleAgeDistribution femaleDistribution = parseFemaleDistribution();
		EmploymentDistribution jobDistribution = parseJobDistribution();
		return new Demography(jobDistribution, hhDistribution, femaleDistribution, maleDistribution);
	}

	private FemaleAgeDistribution parseFemaleDistribution() {
		return new AgeDistributionBuilder(structuralData).buildFemale();
	}

	private MaleAgeDistribution parseMaleDistribution() {
		return new AgeDistributionBuilder(structuralData).buildMale();
	}

	private HouseholdDistribution parseHouseholdDistribution() {
		return new HouseholdDistributionBuilder(structuralData).build();
	}

	private EmploymentDistribution parseJobDistribution() {
		return new EmploymentDistributionBuilder(structuralData).build();
	}

}
