package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.simulation.Employment;

public class EmploymentDistributionBuilder {

	private final AttributeType attributeType;
	private final StructuralData structuralData;

	public EmploymentDistributionBuilder(StructuralData structuralData, AttributeType attributeType) {
		super();
		this.attributeType = attributeType;
		this.structuralData = structuralData;
	}

	public EmploymentDistribution build(String zoneId) {
		if (hasEmploymentInformation()) {
			return createDistribution(zoneId);
		}
		return new EmploymentDistribution();
	}

	private boolean hasEmploymentInformation() {
		return structuralData.getAttributes().stream().anyMatch(a -> a.startsWith(attributeType.attributeName()));
	}

	private EmploymentDistribution createDistribution(String zoneId) {
		EmploymentDistribution jobDistribution = new EmploymentDistribution();
		jobDistribution.addItem(itemFor(Employment.FULLTIME, zoneId, "Job:FullTime"));
		jobDistribution.addItem(itemFor(Employment.PARTTIME, zoneId, "Job:PartTime"));
		jobDistribution.addItem(itemFor(Employment.NONE, zoneId, "Job:None"));
		jobDistribution.addItem(itemFor(Employment.STUDENT_PRIMARY, zoneId, "Job:Education_primary"));
		jobDistribution.addItem(itemFor(Employment.STUDENT_SECONDARY, zoneId, "Job:Education_secondary"));
		jobDistribution.addItem(itemFor(Employment.STUDENT_TERTIARY, zoneId, "Job:Education_tertiary"));
		jobDistribution.addItem(itemFor(Employment.EDUCATION, zoneId, "Job:Education_occup"));
		jobDistribution.addItem(itemFor(Employment.RETIRED, zoneId, "Job:Retired"));
		jobDistribution.addItem(itemFor(Employment.INFANT, zoneId, "Job:Infant"));
		return jobDistribution;
	}

	private EmploymentDistributionItem itemFor(Employment employment, String zoneId, String key) {
		int value = structuralData.valueOrDefault(zoneId, key);
		return new EmploymentDistributionItem(employment, value);
	}

}
