package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;

public class EconomicalStatusDistributionParserTest {

	@Test
	void buildUpDistribution() throws Exception {
		String content = "household_size;economical_status:0-499;economical_status:500-\r\n"
				+ "1,0;1;2\r\n" + "1,5;3;4\r\n";
		RangeDistribution oneDistribution = new RangeDistribution();
		oneDistribution.addItem(new RangeDistributionItem(0, 499, 1));
		oneDistribution.addItem(new RangeDistributionItem(500, Integer.MAX_VALUE, 2));
		RangeDistribution oneHalfDistribution = new RangeDistribution();
		oneHalfDistribution.addItem(new RangeDistributionItem(0, 499, 3));
		oneHalfDistribution.addItem(new RangeDistributionItem(500, Integer.MAX_VALUE, 4));
		TreeMap<Double, RangeDistributionIfc> expected = new TreeMap<>();
		expected.put(1.0d, oneDistribution);
		expected.put(1.5d, oneHalfDistribution);

		TreeMap<Double, RangeDistributionIfc> distribution = parse(content);

		assertThat(distribution).isEqualTo(expected);
	}

	private TreeMap<Double, RangeDistributionIfc> parse(String content) {
		EconomicalStatusDistributionParser parser = new EconomicalStatusDistributionParser();
		return parser.parse(new ByteArrayInputStream(content.toString().getBytes()));
	}
}
