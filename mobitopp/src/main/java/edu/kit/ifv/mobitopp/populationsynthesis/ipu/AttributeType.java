package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;

public interface AttributeType {

  String attributeName();

  String prefix();

  Stream<Attribute> createAttributes(Demography demography, AttributeContext context);

  String createInstanceName(int lowerBound, int upperBound);

  String createInstanceName(RangeDistributionItem item);

}