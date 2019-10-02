package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;

public class EconomicalStatusCalculatorsTest {

	private static final double withinMargin = 1e-6;
	private EconomicalStatusCalculators.DefaultEconomicalStatusCalculator calculator;

	@BeforeEach
	public void initialise() throws URISyntaxException {
		calculator = new EconomicalStatusCalculators.DefaultEconomicalStatusCalculator(EconomicalStatusCalculators.loadMid());
	}
	
	@Test
	void calculatesEconomicalStatus() throws Exception {
		List<TestData> testData = new LinkedList<>();
		testData.add( new TestData(1, 0, 500, EconomicalStatus.veryLow));
		testData.add( new TestData(1, 0, 1000, EconomicalStatus.low));
		testData.add( new TestData(1, 0, 2000, EconomicalStatus.middle));
		testData.add( new TestData(1, 0, 3000, EconomicalStatus.high));
		testData.add( new TestData(1, 0, 4000, EconomicalStatus.veryHigh));
		
		assertAll(testData.stream().map(t -> () -> assertEquals(t.result, calculateStatus(t))));
	}

	private EconomicalStatus calculateStatus(TestData sample) {
		return calculator.calculateFor(sample.nominalSize, sample.numberOfMinors, sample.income);
	}
	
	@Test
	void calculatesOecdWeight() throws Exception {
		assertAll(
				() -> assertEquals(1.0, calculateOecd(1, 0), withinMargin),
				() -> assertEquals(1.5, calculateOecd(2, 0), withinMargin),
				() -> assertEquals(1.3, calculateOecd(2, 1), withinMargin),
				() -> assertEquals(1.6, calculateOecd(3, 2), withinMargin),
				() -> assertEquals(2.1, calculateOecd(4, 2), withinMargin)
				);
	}

	private double calculateOecd(int nominalSize, int numberOfMinors) {
		return calculator.calculateOecdSize(nominalSize, numberOfMinors);
	}

	@AllArgsConstructor
	private static class TestData {
		private int nominalSize;
		private int numberOfMinors;
		private int income;
		private EconomicalStatus result;
	}
}
