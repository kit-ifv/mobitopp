package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Attribute;

public interface AttributeType {

  String attributeName();

  String prefix();

  Stream<Attribute> createAttributes(Demography demography);

}