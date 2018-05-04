package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;

public class DemandZone {

	private final Zone zone;
	private final Demography nominalDemography;
	private final Demography actualDemography;

	public DemandZone(Zone zone, Demography nominalDemand) {
		super();
		this.zone = zone;
		this.nominalDemography = nominalDemand;
		actualDemography = emptyDemographyFrom(nominalDemand);
	}

	private Demography emptyDemographyFrom(Demography nominalDemand) {
		EmploymentDistribution employment = EmploymentDistribution.createDefault();
		HouseholdDistribution household = householdDistribution(nominalDemand);
		FemaleAgeDistribution femaleAge = femaleAgeDistribution(nominalDemand);
		MaleAgeDistribution maleAge = maleAgeDistribution(nominalDemand);
		return new Demography(employment, household, femaleAge, maleAge);
	}

	private FemaleAgeDistribution femaleAgeDistribution(Demography demandModel) {
		FemaleAgeDistribution distribution = new FemaleAgeDistribution();
		demandModel.femaleAge().getItems().stream().map(AgeDistributionItem::createEmpty).forEach(
				distribution::addItem);
		return distribution;
	}

	private MaleAgeDistribution maleAgeDistribution(Demography demandModel) {
		MaleAgeDistribution distribution = new MaleAgeDistribution();
		demandModel.maleAge().getItems().stream().map(AgeDistributionItem::createEmpty).forEach(
				distribution::addItem);
		return distribution;
	}

	private HouseholdDistribution householdDistribution(Demography demandModel) {
		HouseholdDistribution distribution = new HouseholdDistribution();
		demandModel.household().getItems().stream().map(HouseholdDistributionItem::createEmpty).forEach(
				distribution::addItem);
		return distribution;
	}

	public int getOid() {
		return zone.getOid();
	}

	public String getId() {
		return zone.getId();
	}

	public Zone zone() {
		return zone;
	}

	public DataForZone getDemandData() {
		return zone.getDemandData();
	}

	public Demography nominalDemography() {
		return nominalDemography;
	}

	public Demography actualDemography() {
		return actualDemography;
	}

}
