package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

@ExtendWith(MockitoExtension.class)
public class ProcessOnLowerRegionLevelStepTest {

  @Mock
  private PopulationSynthesisStep other;
  @Mock
  private DemandRegion county;
  @Mock
  private DemandRegion community;
  @Mock
  private DemandRegion district;

  @Test
  void processesRegionsOfLowerRegionLevel() throws Exception {
    when(county.regionalLevel()).thenReturn(RegionalLevel.county);
    when(community.regionalLevel()).thenReturn(RegionalLevel.community);
    when(district.regionalLevel()).thenReturn(RegionalLevel.district);
    when(community.parts()).thenReturn(List.of(district));
    when(county.parts()).thenReturn(List.of(community));
    ProcessOnLowerRegionLevelStep step = new ProcessOnLowerRegionLevelStep(other,
        RegionalLevel.district);

    step.process(county);

    verify(other).process(district);
  }
}
