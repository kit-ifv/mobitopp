package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;

@FunctionalInterface
public interface DataFactory {

  StructuralData createData(File file);

}
