package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.simulation.Employment;

public class EmploymentDistributionBuilder {

	private static final String prefix = "job:";
	private final StructuralData structuralData;

	public EmploymentDistributionBuilder(StructuralData structuralData) {
		super();
		this.structuralData = structuralData;
	}

	public EmploymentDistribution build() {
		if (hasEmploymentInformation()) {
			return createDistribution();
		}
		return new EmploymentDistribution();
	}

	private boolean hasEmploymentInformation() {
		return structuralData.getAttributes().stream().anyMatch(a -> a.startsWith(prefix));
	}

	private EmploymentDistribution createDistribution() {
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
		int value = structuralData.valueOrDefault(key);
		return new EmploymentDistributionItem(employment, value);
	}

}
