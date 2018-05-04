package edu.kit.ifv.mobitopp.data.demand;

public class AgeDistributionItem
		implements DemandModelDistributionItemIfc, Comparable<AgeDistributionItem> {
	
	private static final long serialVersionUID = 1L;

	public static enum Type {
		UNTIL, OVER
	}
	
  private final Type sign;
  private final byte age;
  private int amount;

	public AgeDistributionItem(Type sign, int age, int amount) {
		super();
    verify(sign);
    verifyAge(age);
    verifyAmount(amount);
		this.age = (byte) age;
		this.amount = amount;
		this.sign = sign;
	}

	private void verify(Type sign) {
		if (null == sign) {
			throw new IllegalArgumentException("Sign is missing!");
		}
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

	public int age() {
		return this.age;
	}

	public int amount() {
		return this.amount;
	}

	public Type sign() {
		return this.sign;
	}

	public void increment() {
		this.amount += 1;
	}

	public AgeDistributionItem createEmpty() {
		return new AgeDistributionItem(sign, age, 0);
	}  

	public int compareTo(AgeDistributionItem other) {
		if (equals(other)) {
			return 0;
		}

		if ((this.sign == Type.UNTIL) && (other.sign == Type.OVER)) {
			return -1;
		}

		if ((this.sign == other.sign) && (this.age < other.age)) {
			return -1;
		}

		if ((this.sign == other.sign) && (this.age == other.age) && (this.amount < other.amount)) {
			return -1;
		}

		return 1; 
  }

  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + amount;
		result = prime * result + ((sign == null) ? 0 : sign.hashCode());
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
		AgeDistributionItem other = (AgeDistributionItem) obj;
		if (age != other.age)
			return false;
		if (amount != other.amount)
			return false;
		if (sign != other.sign)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [sign=" + sign + ", age=" + age + ", amount=" + amount + "]";
	}
	
	
}
