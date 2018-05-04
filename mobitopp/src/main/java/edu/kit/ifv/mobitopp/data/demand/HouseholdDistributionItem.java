package edu.kit.ifv.mobitopp.data.demand;

public class HouseholdDistributionItem
		implements DemandModelDistributionItemIfc, Comparable<HouseholdDistributionItem> {

	private static final long serialVersionUID = 1L;

	private final int type;
	private int amount;

	public HouseholdDistributionItem(int type, int amount) {
		super();
		verifyType(type);
		verifyAmount(amount);
		this.type = type;
		this.amount = amount;
	}

	private void verifyAmount(int amount) {
		if (0 > amount) {
			throw new IllegalArgumentException(
					"Amount is not allowed to be less than 0, but was: " + amount);
		}
	}

	private void verifyType(int type) {
		if (0 > type) {
			throw new IllegalArgumentException("Type is not allowed to be less than 0, but was: " + type);
		}
	}

	public int type() {
		return this.type;
	}

	public int amount() {
		return this.amount;
	}

	public void increment() {
		this.amount += 1;
	}

	public HouseholdDistributionItem createEmpty() {
		return new HouseholdDistributionItem(type, 0);
	}

	public int compareTo(HouseholdDistributionItem other) {
		if (equals(other)) {
			return 0;
		}

		if (this.type < other.type) {
			return -1;
		}

		if ((this.type == other.type) && (this.amount < other.amount)) {
			return -1;
		}

		return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HouseholdDistributionItem other = (HouseholdDistributionItem) obj;
		if (amount != other.amount)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [type=" + type + ", amount=" + amount + "]";
	}
}
