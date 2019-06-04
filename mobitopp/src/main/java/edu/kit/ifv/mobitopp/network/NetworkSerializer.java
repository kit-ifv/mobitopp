package edu.kit.ifv.mobitopp.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetworkReader;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class NetworkSerializer {

	protected static void writeSimpleNetwork(
		SimpleRoadNetwork network,
		String filename
	) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(network);
			out.close();

		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected static void writeVisumRoadNetwork(
		VisumRoadNetwork network,
		String filename
	) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(network);
			out.close();

		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected static SimpleRoadNetwork deserializeSimpleNetwork(
		String filename
	) {

   FileInputStream fis = null;
   ObjectInputStream in = null;

   SimpleRoadNetwork network = null;

   try
   {
     fis = new FileInputStream(filename);
     in = new ObjectInputStream(fis);
     network = (SimpleRoadNetwork)in.readObject();
     in.close();
   }
   catch(IOException ex)
   {
     ex.printStackTrace();
   }
   catch(ClassNotFoundException e)
   {
     e.printStackTrace();
			System.exit(1);
   }

		return network;
	}

	protected static VisumRoadNetwork deserializeVisumRoadNetwork(
		String filename
	) {

   FileInputStream fis = null;
   ObjectInputStream in = null;

   VisumRoadNetwork network = null;

   try
   {
     fis = new FileInputStream(filename);
     in = new ObjectInputStream(fis);
     network = (VisumRoadNetwork)in.readObject();
     in.close();
   }
   catch(IOException ex)
   {
     ex.printStackTrace();
   }
   catch(ClassNotFoundException e)
   {
     e.printStackTrace();
			System.exit(1);
   }

		return network;
	}

	public static String basename(String filename) {

		int start = 1+filename.lastIndexOf('/');
		int end = filename.lastIndexOf('.');

		return filename.substring(start, end);
	}
	
	
	public static VisumNetwork readVisumNetwork(String filename, NetfileLanguage netfileLanguage) {

		String visumFile = basename(filename) + ".visum_serialized";

System.out.println("filename: " + filename);
System.out.println("serialized file: " + visumFile);
System.out.println("serialized file exists? " + new File(visumFile).isFile());

		VisumNetwork visumNetwork;
	
		if (new File(visumFile).isFile()) {
	
			System.out.println("Deserializing network...");
			visumNetwork = deserializeVisumNetwork(visumFile);
			System.out.println("DONE!");
	
		} else {
	
			System.out.println("reading network...");
			VisumNetworkReader networkReader = new VisumNetworkReader(netfileLanguage);
			visumNetwork = networkReader.readNetwork(filename);
			System.out.println("DONE!");
	
			networkReader = null;
			System.gc();
	
			System.out.println("serializing network...");
			writeVisumNetwork(visumNetwork, visumFile);
			System.out.println("DONE!");
	
		}

		return visumNetwork;
	}
	
	protected static VisumNetwork deserializeVisumNetwork(
		String filename
	) {

   FileInputStream fis = null;
   ObjectInputStream in = null;

   VisumNetwork network = null;

   try
   {
     fis = new FileInputStream(filename);
     in = new ObjectInputStream(fis);
     network = (VisumNetwork)in.readObject();
     in.close();
   }
   catch(IOException ex)
   {
     ex.printStackTrace();
   }
   catch(ClassNotFoundException e)
   {
     e.printStackTrace();
			System.exit(1);
   }

		return network;
	}
	
	protected static void writeVisumNetwork(
		VisumNetwork network,
		String filename
	) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(network);
			out.close();

		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

