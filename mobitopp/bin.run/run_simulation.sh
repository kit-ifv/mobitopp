#!/usr/bin/bash

java -Xmx15000M \
-Xrunhprof:cpu=samples,depth=10 \
-classpath "dist/mobitopp.jar" \
-ea \
edu.kit.ifv.mobitopp.simulation.SimulationConfiguration $*