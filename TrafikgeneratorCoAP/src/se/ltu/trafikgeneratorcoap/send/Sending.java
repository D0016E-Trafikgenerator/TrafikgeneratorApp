package se.ltu.trafikgeneratorcoap.send;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import ch.ethz.inf.vs.californium.coap.CoAP;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.network.CoAPEndpoint;

public class Sending {
	private static int maxpacketsize = 1024;
	private static int headersize = 59;
	private static Context context;
	private static Random random = new Random();
	private static int sentMessages = 0;
	private static long lastSentMessage = 0L;
	public static void sendDataFromApp(TrafficConfig config, Context context) {
		Sending.context = context;
		sendData(config);
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
		/*int controlPort = 5683;
		if (uri.split(":").length == 2) {
			controlPort = Integer.valueOf(uri.split(":")[1]);
			uri = uri.split(":")[0];
		}*/
		int testport = config.getIntegerSetting(Settings.TEST_TESTPORT);
		int payloadSize = config.getIntegerSetting(Settings.TRAFFIC_MESSAGESIZE);
		if (payloadSize + headersize > maxpacketsize) payloadSize = maxpacketsize-headersize;
		int seconds = Math.round(config.getDecimalSetting(Settings.TRAFFIC_MAXSENDTIME));
		int ntpPort = config.getIntegerSetting(Settings.TEST_NTPPORT);
		int maxMessages = config.getIntegerSetting(Settings.TRAFFIC_MAXMESSAGES);
		int sendrate = config.getIntegerSetting(Settings.TRAFFIC_RATE);
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
		try {
			Thread.sleep(100);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
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
				int packetsize = headersize + payloadSize;
				float packetsPerSecond = (float)sendrate / (float)packetsize;
				int timeBetweenPackets = (int) Math.round(1000.0/packetsPerSecond);
				Log.d("dummycoap", "payloadSize: " + payloadSize +
						"\npacketsize: " + packetsize +
						"\nsendrate: " + sendrate + 
						"\npacketsPerSecond: " + packetsPerSecond +
						"\ntimeBetweenPackets: " + timeBetweenPackets + 
						"ms\npackets to send: at most " + Math.round((numberOfTests*seconds*1000)/((float)timeBetweenPackets)) +
						"(or " + maxMessages + ")");
				Thread.sleep(5000);
				CoAPEndpoint dataEndpoint = new CoAPEndpoint(config.toNetworkConfig());
				dataEndpoint.start();
				if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
					if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
						Sending.runTimeTest(dataEndpoint, uri, testport, seconds, type, payloadSize, timeBetweenPackets, timeBetweenTests, numberOfTests);
						testDone = true;
					}
					else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("MESSAGES")) {
						Sending.runMessageTest(dataEndpoint, uri, testport, maxMessages, type, payloadSize, timeBetweenPackets, timeBetweenTests, numberOfTests);
						testDone = true;
					}
					else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("FILETRANSFER")) {
						Sending.runMessageTest(dataEndpoint, uri, testport, maxMessages, type, payloadSize, timeBetweenPackets, timeBetweenTests, numberOfTests);
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
			Integer timeBetweenPackets, Integer timeBetweenTests, Integer numberOfTests) {
		try {
			for (int i = 1; i <= numberOfTests; i++) {
				long endtime = ((long) Math.round(1000 * seconds)) + SystemClock.elapsedRealtime();
				while (SystemClock.elapsedRealtime() < endtime) {
					long timeToNextSend = lastSentMessage + timeBetweenPackets - SystemClock.elapsedRealtime();
					if (timeToNextSend > 0) {
						Thread.sleep(timeToNextSend);
					}
					Request test;
					test = Request.newPost();
					String testURI = "coap://" + uri + ":" + testport + "/test";
					test.setURI(testURI);
					test.setType(type);
					test.setPayload(DummyGenerator.makeDummydata(random.nextLong(), payloadSize));
					test.send(endpoint);
					lastSentMessage = SystemClock.elapsedRealtime();
					/*if (test.isConfirmable())
						while (!test.isAcknowledged() && !test.isTimeouted() && !test.isCanceled() && !test.isRejected())
							Thread.sleep(1);*/
				}
	    		if (i < numberOfTests)
					Thread.sleep(timeBetweenTests);
			}
		} catch (InterruptedException e) {;}
	}
	private static void runMessageTest(CoAPEndpoint endpoint, String uri, Integer testport, Integer maxMessages, CoAP.Type type, Integer payloadSize,
			Integer timeBetweenPackets, Integer timeBetweenTests, Integer numberOfTests) {
		try {
			for (int i = 1; i <= numberOfTests; i++) {
				while (sentMessages < maxMessages) {
					//Log.d("dummycoap", "sentMessages, maxMessages " + sentMessages +" "+ maxMessages);
					long timeToNextSend = lastSentMessage + timeBetweenPackets - SystemClock.elapsedRealtime();
					if (timeToNextSend > 0) {
						Thread.sleep(timeToNextSend);
					}
					Request test;
					test = Request.newPost();
					String testURI = "coap://" + uri + ":" + testport + "/test";
					test.setURI(testURI);
					test.setType(type);
					test.setPayload(DummyGenerator.makeDummydata(random.nextLong(), payloadSize));
//					if ((sentMessages+1) < 10 || (maxMessages-sentMessages) < 10)
//						Log.d("dummycoap", "payloadstring" + test.getPayloadString().substring(0, 50).replaceAll("[\u0000-\u001f]", ""));
					test.send(endpoint);
					sentMessages += 1;
					lastSentMessage = SystemClock.elapsedRealtime();
					if (test.isConfirmable())
						while (!test.isAcknowledged() && !test.isTimeouted() && !test.isCanceled() && !test.isRejected())
							Thread.sleep(1);
				}
	    		if (i < numberOfTests)
					Thread.sleep(timeBetweenTests);
			}
		} catch (InterruptedException e) {;}
	}
}