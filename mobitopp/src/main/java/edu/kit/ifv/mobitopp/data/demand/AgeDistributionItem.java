package edu.kit.ifv.mobitopp.data.demand;

import java.util.Objects;

public class AgeDistributionItem
		implements DemandModelDistributionItemIfc, Comparable<AgeDistributionItem> {
	
	private static final long serialVersionUID = 1L;

  private final int age;
  private final int lowerBound;
  private final int upperBound;
  private int amount;

	public AgeDistributionItem(int lowerBound, int upperBound, int amount) {
		super();
		verifyAge(lowerBound);
    verifyAge(upperBound);
    verifyAmount(amount);
		age = upperBound;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.amount = amount;
	}

	public AgeDistributionItem(AgeDistributionItem other) {
		this(other.lowerBound, other.upperBound, other.amount);
	}

	private void verifyAmount(int amount) {
		if (0 > amount) {
			throw new IllegalArgumentException("Amount is too low: " + amount);
		}
	}

	private void verifyAge(int age) {
		if (0 > age) {
			throw new IllegalArgumentException("Age is too low: " + age);
		}
	}

	public boolean matches(int age) {
		return lowerBound <= age && upperBound >= age;
	}
	
	public int lowerBound() {
		return this.lowerBound;
	}
	
	public int upperBound() {
		return this.upperBound;
	}

	public int amount() {
		return this.amount;
	}

	public void increment() {
		this.amount += 1;
	}

	public AgeDistributionItem createEmpty() {
		return new AgeDistributionItem(lowerBound, upperBound, 0);
	}  

	public int compareTo(AgeDistributionItem other) {
		if (equals(other)) {
			return 0;
		}
		
		if (lowerBound < other.lowerBound) {
			return -1;
		}
		if (upperBound < other.upperBound) {
			return -1;
		}
		if (upperBound > other.upperBound) {
			return 1;
		}
		return amount - other.amount;
  }

  @Override
	public int hashCode() {
		return Objects.hash(age, amount, lowerBound, upperBound);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgeDistributionItem other = (AgeDistributionItem) obj;
		return age == other.age && amount == other.amount && lowerBound == other.lowerBound
				&& upperBound == other.upperBound;
	}

	@Override
	public String toString() {
		return "AgeDistributionItem [age=" + age + ", lowerBound=" + lowerBound + ", upperBound="
				+ upperBound + ", amount=" + amount + "]";
	}
	
	
}
