package se.ltu.trafikgeneratorcoap.send;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.os.SystemClock;

import ch.ethz.inf.vs.californium.coap.CoAP;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.network.CoAPEndpoint;

public class Sending {
	private static int maxpacketsize = 1024;
	private static int headersize = 59;
	        static Context context;
	private static Random random = new Random();
	private static int sentMessages = 0;
	public static void sendData(TrafficConfig config, Context context) {
		Sending.context = context;
		sendData(config);
	}
	public static void abort(TrafficConfig config) {
		;
	}
	public static void sendDataFromPC(TrafficConfig config) {
		sendData(config);
	}
	private static void sendData(TrafficConfig config) {
		SntpClient internetTimeClient = new SntpClient();
		SntpClient internetTimeClient2 = new SntpClient();
		int numberOfTests = config.getIntegerSetting(Settings.TEST_REPEATS);
		int timeBetweenTests = Math.round(config.getDecimalSetting(Settings.TEST_INTERMISSION));
		String uri = config.getStringSetting(Settings.TEST_SERVER);
		int testport = config.getIntegerSetting(Settings.TEST_TESTPORT);
		int payloadSize = config.getIntegerSetting(Settings.TRAFFIC_MESSAGESIZE);
		if (payloadSize + headersize > maxpacketsize) payloadSize = maxpacketsize-headersize;
		int seconds = Math.round(config.getDecimalSetting(Settings.TRAFFIC_MAXSENDTIME));
		int ntpPort = config.getIntegerSetting(Settings.TEST_NTPPORT);
		int maxMessages = config.getIntegerSetting(Settings.TRAFFIC_MAXMESSAGES);
		int sendrate = config.getIntegerSetting(Settings.TRAFFIC_RATE);
		int filesize = config.getIntegerSetting(Settings.TRAFFIC_FILESIZE);
		int blocksize = config.getIntegerSetting(Settings.TRAFFIC_BLOCKSIZE);
		String date = (new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())).format(new Date());
		CoAP.Type type = config.getStringSetting(Settings.COAP_MESSAGETYPE).equals("CON")?CoAP.Type.CON:CoAP.Type.NON;
		//Test protocol 1.3a.2
		
		CoAPEndpoint controlEndpoint = new CoAPEndpoint();
		try {
			controlEndpoint.start();
		} catch (IOException e) {;}
		Request control = Request.newPost();
		String controlPayload = TrafficConfig.networkConfigToStringList(config.toNetworkConfig());
		String controlURI = "coap://" + uri + "/control?time=" + date;
		control.setURI(controlURI);
		control.setPayload(controlPayload);
		control.send(controlEndpoint);
		Response response;
		try {
			response = control.waitForResponse();
			String token = response.getTokenString();
			//Test protocol 1.3a.4 & 1.3a.5
			if (!response.equals(null) && response.getCode().equals(ResponseCode.CREATED) && Logger.start(token, testport)
					&& (internetTimeClient.requestTime(uri.split(":")[0], ntpPort, 1000) || internetTimeClient.requestTime("pool.ntp.org", 123, 1000))) {
				String ntp_uri = internetTimeClient.getHost();
				long ntpError = internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime()
						- internetTimeClient.getNtpTimeReference() - System.currentTimeMillis();
				Meta.beforeTest(config, token, date, ntp_uri, ntpError);
				boolean testDone = false;
				Thread.sleep(5000);
				CoAPEndpoint dataEndpoint = new CoAPEndpoint(config.toNetworkConfig());
				dataEndpoint.start();
				if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
					if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
						Sending.runTimeTest(dataEndpoint, uri, testport, seconds, type, payloadSize, sendrate, timeBetweenTests, numberOfTests);
						testDone = true;
					}
					else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("MESSAGES")) {
						Sending.runMessageTest(dataEndpoint, uri, testport, maxMessages, type, payloadSize, sendrate, timeBetweenTests, numberOfTests);
						testDone = true;
					}
					else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("FILETRANSFER")) {
						Sending.runFileTest(dataEndpoint, uri, testport, filesize, blocksize, sendrate, timeBetweenTests, numberOfTests);
						testDone = true;
					}
				}
				else if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("ONOFF_SOURCE")) {
					;
				}
				if (testDone) {
					//Test protocol 1.3a.6
					control = Request.newDelete();
					control.setURI("coap://" + uri + "/control?" + "token=" + token);
					control.send(controlEndpoint);
					//Test protocol 1.3a.7
					if (internetTimeClient2.requestTime(uri.split(":")[0], ntpPort, 1000))
						internetTimeClient2.requestTime("pool.ntp.org", 123, 1000);
					ntpError = internetTimeClient2.getNtpTime() + SystemClock.elapsedRealtime()
							- internetTimeClient2.getNtpTimeReference() - System.currentTimeMillis();
					Meta.afterTest(token, date, ntp_uri, ntpError);
					response = control.waitForResponse();
					if (!response.equals(null) && response.getCode().equals(ResponseCode.DELETED)) {
						//Test protocol 1.3a.9
						if (Logger.stop() && FileSender.sendMeta(uri, token, date)) {
							Thread.sleep(5000);
							//Test protocol 1.3a.10
							FileSender.sendLog(uri, token, date);
						}
					}
				}
			}
		} catch (InterruptedException e1) {;} catch (IOException e) {;}
	}
	private static void runTimeTest(CoAPEndpoint endpoint, String uri, Integer testport, Integer seconds, CoAP.Type type, Integer payloadSize,
			Integer rate, Integer timeBetweenTests, Integer numberOfTests) {
		int bucketFillDelayInMs;
		if (rate > 0 && payloadSize > 0 && headersize > 0)
			bucketFillDelayInMs = 1000/(rate/(payloadSize+headersize));
		else
			bucketFillDelayInMs = 0;
		boolean tokens = true;
		try {
			for (int i = 1; i <= numberOfTests; i++) {
				long timeToStopTest = ((long) Math.round(1000 * seconds)) + SystemClock.elapsedRealtime();

				long nextTimeToFillBucket = SystemClock.elapsedRealtime() + bucketFillDelayInMs;
				
				Request test = null;
				
				while (SystemClock.elapsedRealtime() < timeToStopTest) {
					if (tokens && test == null) {
						test = Request.newPost();
						String testURI = "coap://" + uri + ":" + testport + "/test";
						test.setURI(testURI);
						test.setType(type);
						test.setPayload(DummyGenerator.makeDummydata(random.nextLong(), payloadSize));
						test.send(endpoint);
						tokens = false;
						if (!test.isConfirmable())
							test = null;
					}
					if (bucketFillDelayInMs > 0 && SystemClock.elapsedRealtime() >= nextTimeToFillBucket) {
						nextTimeToFillBucket += bucketFillDelayInMs;
						tokens = true;
					}
					else if (bucketFillDelayInMs <= 1)
						tokens = true;
					if (test != null && test.isConfirmable() && (test.isAcknowledged() || test.isTimeouted() || test.isCanceled() || test.isRejected()))
						test = null;
				}
	    		if (i < numberOfTests)
					Thread.sleep(timeBetweenTests);
			}
		} catch (InterruptedException e) {;}
	}
	private static void runMessageTest(CoAPEndpoint endpoint, String uri, Integer testport, Integer maxMessages, CoAP.Type type, Integer payloadSize,
			Integer rate, Integer timeBetweenTests, Integer numberOfTests) {
		int bucketFillDelayInMs = 1000/(rate/(payloadSize+headersize));
		boolean tokens = true;
		try {
			for (int i = 1; i <= numberOfTests; i++) {
				long nextTimeToFillBucket = SystemClock.elapsedRealtime() + bucketFillDelayInMs;
				Request test = null;
				while (sentMessages < maxMessages) {
					if (tokens && test == null) {
						test = Request.newPost();
						String testURI = "coap://" + uri + ":" + testport + "/test";
						test.setURI(testURI);
						test.setType(type);
						test.setPayload(DummyGenerator.makeDummydata(random.nextLong(), payloadSize));
						test.send(endpoint);
						tokens = false;
						sentMessages += 1;
					}
					if (SystemClock.elapsedRealtime() >= nextTimeToFillBucket) {
						nextTimeToFillBucket += bucketFillDelayInMs;
						tokens = true;
					}
					if (test != null && test.isConfirmable() && (test.isAcknowledged() || test.isTimeouted() || test.isCanceled() || test.isRejected()))
							test = null;
					
				}
	    		if (i < numberOfTests)
					Thread.sleep(timeBetweenTests);
			}
		} catch (InterruptedException e) {;}
	}
	private static void runFileTest(CoAPEndpoint endpoint, String uri, Integer testport, Integer filesize, Integer blocksize,
			Integer rate, Integer timeBetweenTests, Integer numberOfTests) {
		byte[] dummyfile = DummyGenerator.makeDummydata(random.nextLong(), filesize);
		try {
			for (int i = 1; i <= numberOfTests; i++) {
				//TODO: Implement rate limiting -- by taking test.send(endpoint) in a pausable thread?
				Request test;
				test = Request.newPost();
				String testURI = "coap://" + uri + ":" + testport + "/test";
				test.setURI(testURI);
				test.setPayload(dummyfile);
				test.send(endpoint);
				test.waitForResponse();
	    		if (i < numberOfTests)
					Thread.sleep(timeBetweenTests);
			}
		} catch (InterruptedException e) {;}
	}
}