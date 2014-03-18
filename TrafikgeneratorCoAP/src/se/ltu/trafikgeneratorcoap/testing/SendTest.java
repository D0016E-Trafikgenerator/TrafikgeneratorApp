package se.ltu.trafikgeneratorcoap.testing;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import android.os.SystemClock;
import ch.ethz.inf.vs.californium.coap.CoAP;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.network.CoAPEndpoint;

public class SendTest {
	private static int headersize = 59, maxpacketsize = 1024;
	private static Random random = new Random();
	static void run(TrafficConfig config) throws InterruptedException, IOException {
		CoAPEndpoint dataEndpoint = new CoAPEndpoint(config.toNetworkConfig());
		dataEndpoint.start();
		for (int i = 1; i <= config.getIntegerSetting(Settings.TEST_REPEATS); i++) {
			if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
				if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
					runTimeTest(config, dataEndpoint);
				}
				else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("MESSAGES")) {
					runMessageTest(config, dataEndpoint);
				}
				else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("FILETRANSFER")) {
					runFileTest(config, dataEndpoint);
				}
			}
			else if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("ONOFF_SOURCE")) {
				;
			}
    		if (i < config.getIntegerSetting(Settings.TEST_REPEATS))
				Thread.sleep(Math.round(config.getDecimalSetting(Settings.TEST_INTERMISSION)));
		}
	}
	private static void runTimeTest(TrafficConfig config, CoAPEndpoint endpoint) {
		int payloadsize = config.getIntegerSetting(Settings.TRAFFIC_MESSAGESIZE);
		if (payloadsize+headersize > maxpacketsize)
			payloadsize = maxpacketsize-headersize;
		int bucketFillDelayInMs;
		if (config.getIntegerSetting(Settings.TRAFFIC_RATE) > 0 && payloadsize > 0 && headersize > 0)
			bucketFillDelayInMs = 1000/(config.getIntegerSetting(Settings.TRAFFIC_RATE)/(payloadsize+headersize));
		else
			bucketFillDelayInMs = 0;
		String testURI = String.format(Locale.ROOT, "coap://%1$s:%2$d/test", config.getStringSetting(Settings.TEST_SERVER), config.getIntegerSetting(Settings.TEST_TESTPORT));
		boolean tokens = true;
		CoAP.Type type = config.getStringSetting(Settings.COAP_MESSAGETYPE).equals("CON")?CoAP.Type.CON:CoAP.Type.NON;
		
		long timeToStopTest = ((long) Math.round(1000 * config.getDecimalSetting(Settings.TRAFFIC_MAXSENDTIME))) + SystemClock.elapsedRealtime();

		long nextTimeToFillBucket = SystemClock.elapsedRealtime() + bucketFillDelayInMs;

		//Request test = null;
		
		while (SystemClock.elapsedRealtime() < timeToStopTest) {
			if (tokens) {// && test == null) {
				Request test = Request.newPost();
				test.setURI(testURI);
				test.setType(type);
				test.setPayload(PayloadGenerator.generateRandomData(random.nextLong(), payloadsize));
				test.send(endpoint);
				tokens = false;
				//if (!test.isConfirmable())
				//	test = null;
			}
			if (bucketFillDelayInMs > 0 && SystemClock.elapsedRealtime() >= nextTimeToFillBucket) {
				nextTimeToFillBucket += bucketFillDelayInMs;
				tokens = true;
			}
			else if (bucketFillDelayInMs <= 1)
				tokens = true;
			//if (test != null && test.isConfirmable() && (test.isAcknowledged() || test.isTimedOut() || test.isCanceled() || test.isRejected()))
			//	test = null;
		}
	}
	private static void runMessageTest(TrafficConfig config, CoAPEndpoint endpoint) {
		int payloadsize = config.getIntegerSetting(Settings.TRAFFIC_MESSAGESIZE);
		int bucketFillDelayInMs = 1000/(config.getIntegerSetting(Settings.TRAFFIC_RATE)/(payloadsize+headersize));
		long sentMessages = 0, maxMessages = config.getIntegerSetting(Settings.TRAFFIC_MAXMESSAGES);
		String testURI = String.format(Locale.ROOT, "coap://%1$s:%2$d/test", config.getStringSetting(Settings.TEST_SERVER), config.getIntegerSetting(Settings.TEST_TESTPORT));
		boolean tokens = true;
		CoAP.Type type = config.getStringSetting(Settings.COAP_MESSAGETYPE).equals("CON")?CoAP.Type.CON:CoAP.Type.NON;
		long nextTimeToFillBucket = SystemClock.elapsedRealtime() + bucketFillDelayInMs;
		//Request test = null;
		while (sentMessages < maxMessages) {
			if (tokens) {// && test == null) {
				Request test = Request.newPost();
				test.setURI(testURI);
				test.setType(type);
				test.setPayload(PayloadGenerator.generateRandomData(random.nextLong(), payloadsize));
				test.send(endpoint);
				tokens = false;
				sentMessages += 1;
			}
			if (SystemClock.elapsedRealtime() >= nextTimeToFillBucket) {
				nextTimeToFillBucket += bucketFillDelayInMs;
				tokens = true;
			}
			//if (test != null && test.isConfirmable() && (test.isAcknowledged() || test.isTimedOut() || test.isCanceled() || test.isRejected()))
			//		test = null;
		}
	}
	private static void runFileTest(TrafficConfig config, CoAPEndpoint endpoint) throws InterruptedException {
		byte[] dummyfile = PayloadGenerator.generateRandomData(random.nextLong(), config.getIntegerSetting(Settings.TRAFFIC_FILESIZE));
		//TODO: Implement rate limiting -- by taking test.send(endpoint) in a pausable thread? 
		Request test;
		test = Request.newPost();
		String testURI = String.format(Locale.ROOT, "coap://%1$s:%2$d/test", config.getStringSetting(Settings.TEST_SERVER), config.getIntegerSetting(Settings.TEST_TESTPORT));
		test.setURI(testURI);
		test.setPayload(dummyfile);
		test.send(endpoint);
		test.waitForResponse();
	}
}
