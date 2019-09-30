package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.TreeMap;

import org.assertj.core.util.Files;

import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;

public class EconomicalStatusCalculators {

	public static class DefaultEconomicalStatusCalculator implements EconomicalStatusCalculator {

		private TreeMap<Double, RangeDistributionIfc> distributions;

		public DefaultEconomicalStatusCalculator(TreeMap<Double, RangeDistributionIfc> distributions) {
			this.distributions = distributions;
		}

		@Override
		public EconomicalStatus calculateFor(int nominalSize, int income) {
			return EconomicalStatus.veryLow;
		}
			
	}

	private static final String mid2017File = "economical-status-mid2017.csv";

	public static EconomicalStatusCalculator mid2017() {
		try {
		File file = new File(EconomicalStatusCalculators.class.getResource(mid2017File).toURI());
		TreeMap<Double, RangeDistributionIfc> distributions = new EconomicalStatusDistributionParser().parse(Files.contentOf(file, Charset.defaultCharset()));
		return new DefaultEconomicalStatusCalculator(distributions);
		} catch (URISyntaxException cause) {
			throw new RuntimeException(cause);
		}
	}
}
