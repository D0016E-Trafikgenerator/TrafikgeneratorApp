package se.ltu.trafikgeneratorcoap.send;

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

public class Sending {
	static Context context;
	public static void sendData(final String filename, final Context context) {
		Sending.context = context;
		Runnable testrun = new Runnable() {
			@Override 
			public void run() {
				final TrafficConfig config = new TrafficConfig(TrafficConfig.fileToString(filename));
				SntpClient internetTimeClient = new SntpClient();
				int numberOfTests = config.getIntegerSetting(Settings.TEST_REPEATS);
				int timeBetweenTests = Math.round(config.getDecimalSetting(Settings.TEST_INTERMISSION));
				int timeBetweenPackets = Math.round(config.getDecimalSetting(Settings.TRAFFIC_INTERMISSION));
				String uri = config.getStringSetting(Settings.TEST_SERVER);
				int testport = config.getIntegerSetting(Settings.TEST_TESTPORT);
				int payloadSize = config.getIntegerSetting(Settings.TRAFFIC_MESSAGESIZE);
				int seconds = Math.round(config.getDecimalSetting(Settings.TRAFFIC_MAXSENDTIME));
				int ntpPort = config.getIntegerSetting(Settings.TEST_NTPPORT);
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
						Meta.beforeTest(filename, token, date, ntp_uri, ntpError);
						if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
							if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
								for (int i = 1; i <= numberOfTests; i++) {
									long endtime = ((long) Math.round(1000 * seconds)) + SystemClock.elapsedRealtime();
									while (SystemClock.elapsedRealtime() < endtime) {
										Request test;
										test = Request.newPost();
										test.setURI("coap://" + uri + ":" + testport + "/test");
										test.setType(type);
										test.setPayload(DummyGenerator.makeDummydata((new Random()).nextLong(), payloadSize));
										test.send();
										/*if (test.isConfirmable())
											while (!test.isAcknowledged() && !test.isTimeouted() && !test.isCanceled() && !test.isRejected())
												Thread.sleep(1);*/
										if (timeBetweenPackets >= 0)
											Thread.sleep(timeBetweenPackets+1);
									}
					        		if (i < numberOfTests)
										Thread.sleep(timeBetweenTests);
								}
								//Test protocol 1.3a.6
								control = Request.newDelete();
								control.setURI("coap://" + uri + "/control?" + "token=" + token);
								control.send();
								//Test protocol 1.3a.7
								if (internetTimeClient.requestTime(uri.split(":")[0], ntpPort, 1000))
									internetTimeClient.requestTime("pool.ntp.org", 123, 1000);
								ntpError = internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime()
										- internetTimeClient.getNtpTimeReference() - System.currentTimeMillis();
								Meta.afterTest(token, date, ntp_uri, ntpError);
								response = control.waitForResponse();
								if (!response.equals(null) && response.getCode().equals(ResponseCode.DELETED)) {
									Thread.sleep(500);
									//Test protocol 1.3a.9
									if (Logger.stop() && FileSender.sendLog(uri, token, date)) {
										//Test protocol 1.3a.10
										FileSender.sendMeta(uri, token, date);
									}
								}
							}
						}
					}
				} catch (InterruptedException e1) {;}
			}
		};
		new Thread(testrun).start();
	}
}