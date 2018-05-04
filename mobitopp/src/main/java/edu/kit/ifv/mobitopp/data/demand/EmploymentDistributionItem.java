package edu.kit.ifv.mobitopp.data.demand;

import edu.kit.ifv.mobitopp.simulation.Employment;

public class EmploymentDistributionItem
		implements DemandModelDistributionItemIfc, Comparable<EmploymentDistributionItem> {

	private static final long serialVersionUID = 1L;

	private final Employment type;
  private int amount;

	public EmploymentDistributionItem(Employment type, int amount) {
		super();
    verify(type);
    verifyAmount(amount);
    this.type = type;
    this.amount = amount;
  }

  private void verifyAmount(int amount) {
  	if (0 > amount) {
  		throw new IllegalArgumentException("Amount is not allowed to be less than 0, but was " + amount);
  	}
	}

	private void verify(Employment type) {
  	if (null == type) {
  		throw new IllegalArgumentException("Type is not allowed to be null!");
  	}
	}

	public int getTypeAsInt() {
		return type.getTypeAsInt();
	}

	public int amount() {
		return amount;
	}

	public Employment type() {
		return type;
	}
  
	public void increment() {
		this.amount += 1;
	}

  public EmploymentDistributionItem createEmpty() {
  	return new EmploymentDistributionItem(type, 0);
  }

	public int compareTo(EmploymentDistributionItem other) {
		if (equals(other)) {
			return 0;
		}

		if (this.type.getTypeAsInt() < other.type.getTypeAsInt()) {
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
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		EmploymentDistributionItem other = (EmploymentDistributionItem) obj;
		if (amount != other.amount)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [type=" + type + ", amount=" + amount + "]";
	}
	
}
