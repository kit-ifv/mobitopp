package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;

@ExtendWith(MockitoExtension.class)
public class FilterStepTest {

  @Mock
  private PopulationSynthesisStep other;
  @Mock
  private DemandRegion region;

  @Test
  void executesOther() throws Exception {
    FilterStep step = new FilterStep(other, region -> true);
    
    step.process(region);
    
    verify(other).process(region);
  }
  
  @Test
  void doNotExecutesOther() throws Exception {
    FilterStep step = new FilterStep(other, region -> false);
    
    step.process(region);
    
    verifyZeroInteractions(other);
  }
}
