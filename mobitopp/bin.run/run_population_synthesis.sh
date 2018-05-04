#!/usr/bin/bash

java -Xmx22000M -Xss512M -ea \
-classpath "dist/mobitopp.jar" \
edu.kit.ifv.mobitopp.populationsynthesis.PopulationSynthesisConfiguration $*