package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

public class HouseholdConstraint extends BaseConstraint implements Constraint {

	private final String attribute;

	public HouseholdConstraint(int requestedWeight, String attribute) {
		super(requestedWeight);
		this.attribute = attribute;
	}

	@Override
	protected boolean matches(WeightedHousehold household) {
		return 0 < household.attribute(attribute);
	}

	@Override
	protected double totalWeight(WeightedHousehold household) {
		return household.weight();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HouseholdConstraint other = (HouseholdConstraint) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HouseholdConstraint [attribute=" + attribute + ", " + super.toString() + "]";
	}

}
