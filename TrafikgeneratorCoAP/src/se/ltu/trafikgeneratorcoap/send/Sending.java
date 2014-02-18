package se.ltu.trafikgeneratorcoap.send;

import java.util.Random;

import android.os.SystemClock;
import android.util.Log;

import ch.ethz.inf.vs.californium.coap.CoAP;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;

public class Sending {	
	public static void sendData(final String filename) {
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
				CoAP.Type type = config.getStringSetting(Settings.COAP_MESSAGETYPE).equals("CON")?CoAP.Type.CON:CoAP.Type.NON;
				//Test protocol 1.2b.2
				if (true || internetTimeClient.requestTime(uri, 1000)) {
					long ntp_offset = internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime()
							- internetTimeClient.getNtpTimeReference() - System.currentTimeMillis();
					ntp_offset = 0;
					//Test protocol 1.2b.3
					Request control = Request.newPost();
					String controlPayload = 
							"TYPE=" + config.getStringSetting(Settings.COAP_MESSAGETYPE) +
							",NTP_OFFSET=" + Long.toString(ntp_offset) + ";" + 
							TrafficConfig.networkConfigToStringList(config.toNetworkConfig());
					control.setURI("coap://" + uri + "/control");
					control.setPayload(controlPayload);
					control.send();
					Response response;
					try {
						response = control.waitForResponse();
						String token = response.getTokenString();
						//Test protocol 1.2b.5
						if (!response.equals(null) && response.getCode().equals(ResponseCode.CREATED) && Logger.start(token, testport)) {
							Thread.sleep(500);
							if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
								if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
									for (int i = 1; i <= numberOfTests; i++) {
										long endtime = (internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime()
												- internetTimeClient.getNtpTimeReference()) +  ((long) Math.round(1000 * seconds));
										endtime = ((long) Math.round(1000 * seconds)) + SystemClock.elapsedRealtime();
										Request test;
										while (SystemClock.elapsedRealtime() < endtime) {
											test = Request.newPost();
											test.setURI("coap://" + uri + ":" + testport + "/test");
											test.setType(type);
											test.setPayload(DummyGenerator.makeDummydata((new Random()).nextLong(), payloadSize));
											test.send();
											if (test.isConfirmable())
												while (!test.isAcknowledged() && !test.isTimeouted() && !test.isCanceled() && !test.isRejected())
													Thread.sleep(1);
											if (timeBetweenPackets >= 0)
												Thread.sleep(timeBetweenPackets+1);
										}
						        		//Thread.sleep(10);
						        		if (i < numberOfTests)
						        			Thread.sleep(timeBetweenTests);
									}
								}
							}
							//Test protocol 1.2b.6
							ntp_offset = internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime()
									- internetTimeClient.getNtpTimeReference() - System.currentTimeMillis();
							ntp_offset = 0;
							control = Request.newDelete();
							control.setURI("coap://" + uri + "/control?" + "token=" + token);
							control.setPayload("NTP_OFFSET=" + Long.toString(ntp_offset));
							control.send();
							//Test protocol 1.2b.8
							response = control.waitForResponse();
							if (!response.equals(null) && response.getCode().equals(ResponseCode.DELETED)) {
								Thread.sleep(500);
								if (Logger.stop() && FileSender.sendLog(uri, token)) {
									;
								}
							}
						}
					} catch (InterruptedException e) {;}
				}
			}
		};
		new Thread(testrun).start();
	}
}