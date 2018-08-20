package edu.kit.ifv.mobitopp.data.local.serialiser;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.local.serialiser.StationBasedCarSharingOrganizationFormat;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class StationBasedCarSharingOrganizationFormatTest {

	private static final String company = "My Company";
	private StationBasedCarSharingOrganizationFormat format;

	@Before
	public void initialise() {
		format = new StationBasedCarSharingOrganizationFormat();
	}
	
	@Test
	public void prepareCompany() {
		List<String> prepare = format.prepare(organization());

		assertThat(prepare, contains(company));
	}

	private StationBasedCarSharingOrganization organization() {
		return new StationBasedCarSharingOrganization(company);
	}

	@Test
	public void parseCompany() {
		Optional<StationBasedCarSharingOrganization> parsed = format.parse(format.prepare(organization()));
		
		assertThat(parsed, hasValue(organization()));
	}
}
