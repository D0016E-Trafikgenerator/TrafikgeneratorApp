package se.ltu.trafikgeneratorcoap.send;

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

public class Sending {
	private static int maxpacketsize = 1024;
	private static int headersize = 59;
	private static Context context;
	private static Random random = new Random();
	private static int sentMessages = 0;
	private static long lastSentMessage = 0L;
	public static void sendData(TrafficConfig config, Context context) {
		//Log.d("dummycoap", "");
		Sending.context = context;
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
		String date = (new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())).format(new Date());
		CoAP.Type type = config.getStringSetting(Settings.COAP_MESSAGETYPE).equals("CON")?CoAP.Type.CON:CoAP.Type.NON;
		//Test protocol 1.3a.2
		Request control = Request.newPost();
		String controlPayload = TrafficConfig.networkConfigToStringList(config.toNetworkConfig());
		control.setURI("coap://" + uri + "/control?time=" + date);
		control.setPayload(controlPayload);
		control.send();
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
				if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
					if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
						Sending.runTimeTest(seconds, uri, testport, type, payloadSize, timeBetweenPackets, timeBetweenTests, numberOfTests);
						testDone = true;
					}
					else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("MESSAGES")) {
						Sending.runMessageTest(maxMessages, uri, testport, type, payloadSize, timeBetweenPackets, timeBetweenTests, numberOfTests);
						testDone = true;
					}
					else if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("FILETRANSFER")) {
						Sending.runMessageTest(maxMessages, uri, testport, type, payloadSize, timeBetweenPackets, timeBetweenTests, numberOfTests);
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
					control.send();
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
		} catch (InterruptedException e1) {;}
	}
	private static void runTimeTest(Integer seconds, String uri, Integer testport, CoAP.Type type, Integer payloadSize,
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
					test.setURI("coap://" + uri + ":" + testport + "/test");
					test.setType(type);
					test.setPayload(DummyGenerator.makeDummydata(random.nextLong(), payloadSize));
					test.send();
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
	private static void runMessageTest(Integer maxMessages, String uri, Integer testport, CoAP.Type type, Integer payloadSize,
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
					test.setURI("coap://" + uri + ":" + testport + "/test");
					test.setType(type);
					test.setPayload(DummyGenerator.makeDummydata(random.nextLong(), payloadSize));
//					if ((sentMessages+1) < 10 || (maxMessages-sentMessages) < 10)
//						Log.d("dummycoap", "payloadstring" + test.getPayloadString().substring(0, 50).replaceAll("[\u0000-\u001f]", ""));
					test.send();
					sentMessages += 1;
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
}