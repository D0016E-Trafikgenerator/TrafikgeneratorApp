package se.ltu.trafikgeneratorcoap.send;

import java.util.Arrays;
import java.util.Random;

public class DummyGenerator {
	public static byte[] makeDummydata(long seed, int size) {
		Random rnd = new Random(seed);
		byte[] dummydata = new byte[size];
		rnd.nextBytes(dummydata);
		return dummydata;
	}
	public static boolean checkDummydata(long seed, byte[] dummydata) {
		Random rnd = new Random(seed);
		byte[] localDummydata = new byte[dummydata.length];
		rnd.nextBytes(localDummydata);
		return Arrays.equals(dummydata, localDummydata);
	}
}
