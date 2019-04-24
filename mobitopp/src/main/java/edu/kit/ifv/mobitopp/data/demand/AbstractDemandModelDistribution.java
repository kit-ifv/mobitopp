package edu.kit.ifv.mobitopp.data.demand;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public abstract class AbstractDemandModelDistribution<T extends DemandModelDistributionItemIfc & Comparable<T>>
		implements DemandModelDistributionIfc<T> {

	private static final long serialVersionUID = 1L;

	private final SortedSet<T> items;
	
	public AbstractDemandModelDistribution() {
		super();
		items = new TreeSet<>();
	}

	protected SortedSet<T> getItemsInternal() {
		return this.items;
	}

	private void verify(T item) {
		if (null == item) {
			throw new IllegalArgumentException("Incorrect item: " + item);
		}
	}

	public void addItem(T item) {
		verify(item);

		boolean addFlag = getItemsInternal().add(item);

		if (!addFlag) {
			throw new IllegalStateException("Could not add item to distribution: " + item);
		}
	}
	
	@Override
	public Stream<T> items() {
		return this.items.stream();
	}

	public SortedSet<T> getItems() {
		return getItemsInternal();
	}
  
  @Override
  public boolean isEmpty() {
    return items.isEmpty();
  }

	public List<T> getItemsReverse() {
		List<T> elems = new ArrayList<>(getItemsInternal());
		List<T> array = new ArrayList<>();
		for (int i = elems.size() - 1; i >= 0; i--) {
			array.add(elems.get(i));
		}
		return array;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		AbstractDemandModelDistribution<?> other = (AbstractDemandModelDistribution<?>) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [items=" + items + "]";
	}
}
