package edu.kit.ifv.mobitopp.dataimport;

import java.util.Optional;
import java.util.function.Consumer;

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
		Consumer<EmploymentDistributionItem> addItem = jobDistribution::addItem;
		
		itemFor(Employment.FULLTIME, zoneId, "job:FullTime").ifPresent(addItem);;
		itemFor(Employment.PARTTIME, zoneId, "job:PartTime").ifPresent(addItem);;
		itemFor(Employment.NONE, zoneId, "job:None").ifPresent(addItem);;
		itemFor(Employment.STUDENT_PRIMARY, zoneId, "job:Education_primary").ifPresent(addItem);;
		itemFor(Employment.STUDENT_SECONDARY, zoneId, "job:Education_secondary").ifPresent(addItem);;
		itemFor(Employment.STUDENT_TERTIARY, zoneId, "job:Education_tertiary").ifPresent(addItem);;
		itemFor(Employment.EDUCATION, zoneId, "job:Education_occup").ifPresent(addItem);;
		itemFor(Employment.RETIRED, zoneId, "job:Retired").ifPresent(addItem);;
		itemFor(Employment.INFANT, zoneId, "job:Infant").ifPresent(addItem);
		
		return jobDistribution;
	}

	private Optional<EmploymentDistributionItem> itemFor(Employment employment, String zoneId, String key) {
		if (structuralData.hasValue(zoneId, key)) {
			int value = structuralData.valueOrDefault(zoneId, key);
			return Optional.of(new EmploymentDistributionItem(employment, value));
		} else {
			return Optional.empty();
		}
		
	}

}