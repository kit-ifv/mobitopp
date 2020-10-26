package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

@ExtendWith(MockitoExtension.class)
public class OnlyAllZonesCalculatorTest {

  @Mock
  private DemandDataForDemandRegionCalculator other;
  @Mock
  private DemandRegion region;
  @Mock
  private ImpedanceIfc impedance;

  @Test
  void executeIfAccepted() throws Exception {
    DemandDataForDemandRegionCalculator calculator = new OnlyAllZonesCalculator(other, region -> false);
    
    calculator.calculateDemandData(region, impedance);
    
    verify(other).calculateDemandData(region, impedance);
  }
  
  @Test
  void doNothingIfNotAccepted() throws Exception {
    DemandDataForDemandRegionCalculator calculator = new OnlyAllZonesCalculator(other, region -> true);
    
    calculator.calculateDemandData(region, impedance);
    
    verify(other).calculateDemandData(region, impedance);
  }
}
