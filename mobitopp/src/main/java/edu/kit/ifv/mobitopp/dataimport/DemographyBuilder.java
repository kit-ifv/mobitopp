package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.simulation.Employment;

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
		return new Demography(jobDistribution, hhDistribution, femaleDistribution,
				maleDistribution);
	}

	private FemaleAgeDistribution parseFemaleDistribution() {
		return new AgeDistributionBuilder(structuralData).buildFor("Age:F:",
				FemaleAgeDistribution::new);
	}

	private MaleAgeDistribution parseMaleDistribution() {
		return new AgeDistributionBuilder(structuralData).buildFor("Age:M:", MaleAgeDistribution::new);
	}

	private HouseholdDistribution parseHouseholdDistribution() {
		HouseholdDistribution distribution = new HouseholdDistribution();
		for (int code = 1; code <= 12; code++) {
			int amount = valueOrDefault("HHTyp:" + code);
			HouseholdDistributionItem hhItem = new HouseholdDistributionItem(code, amount);
			distribution.addItem(hhItem);
		}
		return distribution;
	}

	private EmploymentDistribution parseJobDistribution() {
		EmploymentDistribution jobDistribution = new EmploymentDistribution();
		jobDistribution.addItem(itemFor(Employment.FULLTIME, "Job:FullTime"));
		jobDistribution.addItem(itemFor(Employment.PARTTIME, "Job:PartTime"));
		jobDistribution.addItem(itemFor(Employment.NONE, "Job:None"));
		jobDistribution.addItem(itemFor(Employment.STUDENT_PRIMARY, "Job:Education_primary"));
		jobDistribution.addItem(itemFor(Employment.STUDENT_SECONDARY, "Job:Education_secondary"));
		jobDistribution.addItem(itemFor(Employment.STUDENT_TERTIARY, "Job:Education_tertiary"));
		jobDistribution.addItem(itemFor(Employment.EDUCATION, "Job:Education_occup"));
		jobDistribution.addItem(itemFor(Employment.RETIRED, "Job:Retired"));
		jobDistribution.addItem(itemFor(Employment.INFANT, "Job:Infant"));
		return jobDistribution;
	}

	private EmploymentDistributionItem itemFor(Employment employment, String key) {
		return new EmploymentDistributionItem(employment, valueOrDefault(key));
	}

	private int valueOrDefault(String key) {
		return structuralData.valueOrDefault(key);
	}
}
