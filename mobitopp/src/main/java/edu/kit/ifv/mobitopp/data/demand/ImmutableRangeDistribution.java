package edu.kit.ifv.mobitopp.data.demand;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class ImmutableRangeDistribution implements RangeDistributionIfc {

	private final RangeDistributionIfc other;
	
	@Override
	public void addItem(RangeDistributionItem item) {
		throwImmutableException();
	}

	private void throwImmutableException() {
		throw new UnsupportedOperationException("Immutable distribution can not be changed!");
	}

	@Override
	public SortedSet<RangeDistributionItem> getItems() {
		return other.getItems();
	}

	@Override
	public List<RangeDistributionItem> getItemsReverse() {
		return other.getItemsReverse();
	}

	@Override
	public Stream<RangeDistributionItem> items() {
		return other.items();
	}

	@Override
	public int getTotalAmount() {
		return other.getTotalAmount();
	}

	@Override
	public boolean isEmpty() {
		return other.isEmpty();
	}

	@Override
	public RangeDistributionItem getItem(int value) {
		return other.getItem(value);
	}

	@Override
	public boolean hasItem(int value) {
		return other.hasItem(value);
	}

	@Override
	public RangeDistributionIfc createEmpty() {
		return other.createEmpty();
	}

	@Override
	public void increment(int value) {
		throwImmutableException();
	}

	@Override
	public int amount(int value) {
		return other.amount(value);
	}

}
