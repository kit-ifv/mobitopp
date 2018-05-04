package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.Closeable;
import java.io.IOException;

abstract class BaseSerializer implements Closeable {
	
	public abstract void writeHeader() throws IOException;

}