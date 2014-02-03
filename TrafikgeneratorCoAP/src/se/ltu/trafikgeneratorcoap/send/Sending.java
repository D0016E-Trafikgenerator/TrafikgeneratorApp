package se.ltu.trafikgeneratorcoap.send;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import android.os.SystemClock;
import android.util.Log;

import ch.ethz.inf.vs.californium.coap.CoAP;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionSet;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.network.config.NetworkConfig;

public class Sending {
	static TrafficConfig config;
	
	public static void sendData(String filename) {
		filename = "/storage/sdcard0/Download/SampleConfig.cfg";
		Log.i("dummycoap", "START: \"" + filename + "\"");

		FileReader fil;
		StringBuilder  stringBuilder;
		try {
			fil = new FileReader (filename);
			BufferedReader reader        = new BufferedReader( fil);
		    String         line          = null;
		                   stringBuilder = new StringBuilder();
		    String         ls            = System.getProperty("line.separator");
			while( ( line = reader.readLine() ) != null ) {
			    stringBuilder.append( line );
			    stringBuilder.append( ls );
			}
			reader.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
		
		config = new TrafficConfig(stringBuilder.toString());
		Log.i("dummycoap", "Trying to start test \"" + config.getStringSetting(Settings.META_TITLE) + "\"...");
		Runnable testrun = new Runnable() { 
            @Override 
            public void run() {
            	// 1: Set variables.
            	SntpClient internetTimeClient = new SntpClient();
            	int numberOfTests = config.getIntegerSetting(Settings.TEST_REPEATS);
            	int timeBetweenTests = Math.round(config.getDecimalSetting(Settings.TEST_INTERMISSION));
            	int timeBetweenPackets = Math.round(config.getDecimalSetting(Settings.TRAFFIC_INTERMISSION));
        		String uri = config.getStringSetting(Settings.TEST_SERVER);
        		int testport = config.getIntegerSetting(Settings.TEST_TESTPORT);
        		int payloadSize = config.getIntegerSetting(Settings.TRAFFIC_MESSAGESIZE); /* */
        		int seconds = Math.round(config.getDecimalSetting(Settings.TRAFFIC_MAXSENDTIME));
        		
        		Log.i("dummycoap", "Starting test \"" + config.getStringSetting(Settings.META_TITLE) + "\"!");
        		
        		Request controlMessage = Request.newPost();
        		controlMessage.setURI(uri + "/control");
        		OptionSet testServerOptions = controlMessage.getOptions();
        		
        		// Set Trafikgenerator options to send to & request of the control server.
        		Option portOption = new Option();
        		portOption.setNumber(65000);
        		portOption.setIntegerValue(testport);
        		testServerOptions.addOption(portOption);

        		Option timeoutOption = new Option();
        		timeoutOption.setNumber(65002);
        		timeoutOption.setIntegerValue(config.getIntegerSetting(Settings.COAP_ACK_TIMEOUT));
        		testServerOptions.addOption(timeoutOption);

        		Option randomOption = new Option();
        		randomOption.setNumber(65003);
        		randomOption.setStringValue(Float.toString(config.getDecimalSetting(Settings.COAP_ACK_RANDOM_FACTOR)));
        		testServerOptions.addOption(randomOption);

        		Option retransmitOption = new Option();
        		retransmitOption.setNumber(65004);
        		retransmitOption.setIntegerValue(config.getIntegerSetting(Settings.COAP_MAX_RETRANSMIT));
        		testServerOptions.addOption(retransmitOption);

        		Option nstartOption = new Option();
        		nstartOption.setNumber(65005);
        		nstartOption.setIntegerValue(config.getIntegerSetting(Settings.COAP_NSTART));
        		testServerOptions.addOption(nstartOption);

        		Option probingOption = new Option();
        		probingOption.setNumber(65006);
        		probingOption.setStringValue(Float.toString(config.getIntegerSetting(Settings.COAP_PROBING_RATE)));
        		testServerOptions.addOption(probingOption);

        		Option controlStartOption = new Option();
        		controlStartOption.setNumber(65008);
        		testServerOptions.addOption(controlStartOption);
        		
        		controlMessage.setOptions(testServerOptions);
        		controlMessage.send();
        		
        		Log.i("dummycoap", "TEST BEGINS");
            	
        		try {
					Response controlResponse = controlMessage.waitForResponse();
	        		String testToken = controlResponse.getTokenString();
					if (controlResponse != null && controlResponse.getCode() == ResponseCode.CREATED) {
		            	Logger testLog = new Logger(config, "/storage/sdcard0/Download/JustALog.log");
		        		Log.i("dummycoap", "TEST HAS BEGUN");
        				if (internetTimeClient.requestTime("0.se.pool.ntp.org", 1000)) {
        					long now = internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference();
        				}
		        		for (int i = 1; i <= numberOfTests; i++) {
		            		//Log.i("dummycoap", "TEST "+ i + "/" + numberOfTests + " (token: " + testToken + ")");
			        		NetworkConfig customSettings = NetworkConfig.createStandardWithoutFile();
			        		//customSettings.setInt("ACK_TIMEOUT", config.getIntegerSetting(Settings.COAP_ACK_TIMEOUT));
			        		//customSettings.setFloat("ACK_RANDOM_FACTOR", config.getDecimalSetting(Settings.COAP_ACK_RANDOM_FACTOR));
			        		//customSettings.setInt("MAX_RETRANSMIT", config.getIntegerSetting(Settings.COAP_MAX_RETRANSMIT));
			        		//customSettings.setInt("NSTART", config.getIntegerSetting(Settings.COAP_NSTART));
			        		//customSettings.setFloat("PROBING_RATE", config.getIntegerSetting(Settings.COAP_PROBING_RATE));
			        		
			        					        		
			        		if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
			        			//Log.i("dummycoap", "TEST asdfasd");
			        			if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
			        				Log.i("dummycoap", "TEST hurpadurpa");
			        				long endtime = (internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference()) +  ((long) Math.round(1000 * config.getDecimalSetting(Settings.TRAFFIC_MAXSENDTIME)));
			        				int j = 1;
			        				Request testRequest;
			        				while ((internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference()) < endtime) {
						        		//Log.i("dummycoap", "New request to " + uri + ":" + testport + "/dummydata");
					        			testRequest = Request.newPost();
						        		testRequest.setURI(uri + ":" + testport + "/dummydata");
						        		//testRequest.setType(CoAP.Type.valueOf(config.getStringSetting(Settings.COAP_MESSAGETYPE)));
						        		testRequest.setType(CoAP.Type.NON);
			        					//Client initierar test (börjar skicka meddelanden) och loggar sina sändningar. I början av loggfilen står det “TOKEN: 0x7F”
			        					//To avoid retransmission limits:
					            		//Log.i("dummycoap", "TEST " + testToken + " PACKET " + (j++));
			        					//int MID = testRequest.getMID();
			        					/*if (MID < 65535)
			        						MID += 1;
			        					else
			        						MID = 0;*/
			        					testRequest.setPayload(DummyGenerator.makeDummydata((new Random()).nextLong(), payloadSize));
			        					//testRequest.setMID(MID);
			        					//testRequest.setToken(randomToken);
			        					//Log.i("dummycoap", "MID is" + testRequest.getMID());
			        					testRequest.send();
						        		long now = (internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference());
						        		Thread.sleep(9);
			        					testLog.log(now, testRequest.getType().toString(), testRequest.getMID(),
			        							testRequest.getType().toString(), testRequest.getPayloadSize(), 123, testRequest.getTokenString());
			        					//Log.i("dummycoap", "Time to sleep for " + timeBetweenPackets + " ms...");
			        					if (timeBetweenPackets >= 0)
			        						Thread.sleep(timeBetweenPackets+1);
									/*
			        					TRAFFIC       TYPE=CONSTANT_SOURCE       #=default
			        					TRAFFIC       MODE=TIME                  #=default
			        					TRAFFIC       MAXSENDTIME=10.0           #=default; in seconds
			        					TRAFFIC       RATE=25000                 #=default; in bytes/second
			        					TRAFFIC       MESSAGESIZE=100            #=default; in bytes
			        					TRAFFIC       INTERMISSION=0.0           #=default; in ms; between messages
			        					TRAFFIC       RANDOMFACTOR=1.0           #=default; varies INTERMISSION
									*/
			        				}
			        			}
			        		}
			        		testLog.flush();
			        		Thread.sleep(10);
			        		if (i < numberOfTests) {
			        			Log.i("dummycoap", "Time to sleep for " + timeBetweenTests + " ms...");
			        			Thread.sleep(timeBetweenTests);
			        		}
		            	}
		        		Log.i("dummycoap", "All messages sent!");
		            	

		        		controlMessage = Request.newPost();
		        		controlMessage.setURI(uri + "/control");
		        		testServerOptions = controlMessage.getOptions();
		        		
		        		Option controlStopOption = new Option();
		        		controlStopOption.setNumber(65009);
		        		testServerOptions.addOption(controlStopOption);
		        		
		        		controlMessage.setOptions(testServerOptions);
		        		controlMessage.send();
		        		
		            	//TEST OVER
    					/*
    					client får ResponseCode.DELETED

    					Client skickar logfil till server på coap://server:5683/fileserver?type=log 
    					*/
					}
				} catch (InterruptedException e1) {
					;
				}
            }
		};
		new Thread( testrun ).start();
	}
}