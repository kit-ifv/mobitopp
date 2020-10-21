package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

@ExtendWith(MockitoExtension.class)
public class AdaptiveCalculatorTest {

  @Mock
  private DemandDataForDemandRegionCalculator other;
  @Mock
  private ImpedanceIfc impedance;
  @Mock
  private DemandRegion region;
  @Mock
  private DemandRegion somePart;
  @Mock
  private DemandRegion otherPart;

  @Test
  void callsOtherCalculatorForAllPartsOfTheRegion() throws Exception {
    AdaptiveCalculator calculator = new AdaptiveCalculator(other,
        region -> Stream.of(somePart, otherPart));

    calculator.calculateDemandData(region, impedance);

    verify(other).calculateDemandData(somePart, impedance);
    verify(other).calculateDemandData(otherPart, impedance);
  }
}
