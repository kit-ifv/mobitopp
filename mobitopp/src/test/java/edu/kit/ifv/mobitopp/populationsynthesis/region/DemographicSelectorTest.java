package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.demand.Demography;

@ExtendWith(MockitoExtension.class)
public class DemographicSelectorTest {

	@Mock
	private DemandRegion input;
	@Mock
	private DemandRegion part;
	@Mock
	private Demography filledDemography;
	@Mock
	private Demography emptyDemography;
	
	@BeforeEach
	public void initialise() {
		when(filledDemography.hasData()).thenReturn(true);
	}

	@Test
	void selectGivenRegionWhenItHasDemographicData() throws Exception {
		when(input.nominalDemography()).thenReturn(filledDemography);
		DemographicSelector selector = new DemographicSelector();
		
		List<DemandRegion> output = selector.select(input).collect(toList());
		
		assertThat(output).contains(input);
		verify(input).nominalDemography();
		verify(filledDemography).hasData();
	}
	
	@Test
	void selectPartsWhenRegionHasNoDemographicData() throws Exception {
		when(emptyDemography.hasData()).thenReturn(false);
		when(input.nominalDemography()).thenReturn(emptyDemography);
		when(part.nominalDemography()).thenReturn(filledDemography);
		when(input.parts()).thenReturn(List.of(part));
		DemographicSelector selector = new DemographicSelector();
		
		List<DemandRegion> output = selector.select(input).collect(toList());
		
		assertThat(output).contains(part);
		verify(emptyDemography).hasData();
		verify(filledDemography).hasData();
	}
	
}
