package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.Map;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public class MobilityProviderAssigner implements Consumer<PersonBuilder> {

  private final Map<String, MobilityProviderCustomerModel> models;

  public MobilityProviderAssigner(final Map<String, MobilityProviderCustomerModel> models) {
    this.models = models;
  }

  @Override
  public void accept(final PersonBuilder person) {
    this.models.forEach((company, model) -> assign(person, model, company));
  }

  private void assign(
      final PersonBuilder person, final MobilityProviderCustomerModel model, final String company) {
    person.setMobilityProviderMembership(company, model.estimateCustomership(person));
  }

}
