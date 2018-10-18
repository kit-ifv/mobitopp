package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.simulation.Employment;

public class PrintEmploymentTest {

	private EmploymentDistribution total;
	private DemandZoneRepository zoneRepository;
	private Logger toOut;

	@Before
	public void initialise() {
		zoneRepository = mock(DemandZoneRepository.class);
		toOut = mock(Logger.class);

		total = new EmploymentDistribution();
		total.addItem(new EmploymentDistributionItem(Employment.NONE, 2));
		total.addItem(new EmploymentDistributionItem(Employment.PARTTIME, 3));
		total.addItem(new EmploymentDistributionItem(Employment.FULLTIME, 4));
	}

	@Test
	public void printsEachDistributionItem() {
		PrintEmployment printer = newPrinter();

		printer.print(total);

		verify(toOut).println(Employment.NONE.getTypeAsString() + ": " + 2);
		verify(toOut).println(Employment.PARTTIME.getTypeAsString() + ": " + 3);
		verify(toOut).println(Employment.FULLTIME.getTypeAsString() + ": " + 4);
	}

	@Test
	public void printsTotalAmount() {
		PrintEmployment printer = newPrinter();

		printer.print(total);

		verify(toOut).println("TOTAL\t" + 9);
	}

	private PrintEmployment newPrinter() {
		return new PrintEmployment(zoneRepository, toOut);
	}
}
