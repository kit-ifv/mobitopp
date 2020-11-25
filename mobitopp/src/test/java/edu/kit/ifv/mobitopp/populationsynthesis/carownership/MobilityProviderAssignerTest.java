package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

@ExtendWith(MockitoExtension.class)
public class MobilityProviderAssignerTest {

  @Mock
  private MobilityProviderCustomerModel someModel;
  @Mock
  private PersonBuilder person;

  @Test
  void assignNewMembership() throws Exception {
    MobilityProviderAssigner assigner = new MobilityProviderAssigner(Map.of("some", someModel));
    
    assigner.accept(person);
    
    verify(someModel).estimateCustomership(person);
    verify(person).setMobilityProviderMembership("some", true);
  }
}
