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
		//filename = "/storage/sdcard0/Download/SampleConfig.cfg";

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
        		
        		Request controlMessage = Request.newPost();
        		controlMessage.setURI("coap://" + uri + "/control");
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
						String command = "su && tcpdump-coap -s 65535 -w /storage/sdcard0/Download/" + testToken + ".pcap 'port " + testport + "' &";
						Runtime.getRuntime().exec(command);
						Thread.sleep(100);
        				if (internetTimeClient.requestTime("0.se.pool.ntp.org", 1000)) {
        					;//long now = internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference();
        				}
		        		for (int i = 1; i <= numberOfTests; i++) {
			        		//NetworkConfig customSettings = NetworkConfig.createStandardWithoutFile();
			        		if (config.getStringSetting(Settings.TRAFFIC_TYPE).equals("CONSTANT_SOURCE")) {
			        			if (config.getStringSetting(Settings.TRAFFIC_MODE).equals("TIME")) {
			        				long endtime = (internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference()) +  ((long) Math.round(1000 * seconds));
			        				Request testRequest;
			        				while ((internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference()) < endtime) {
					        			testRequest = Request.newPost();
						        		testRequest.setURI(uri + ":" + testport + "/dummydata");
						        		testRequest.setType(CoAP.Type.NON);
			        					testRequest.setPayload(DummyGenerator.makeDummydata((new Random()).nextLong(), payloadSize));
			        					testRequest.send();
						        		//long now = (internetTimeClient.getNtpTime() + SystemClock.elapsedRealtime() - internetTimeClient.getNtpTimeReference());
						        		//Thread.sleep(9);
			        					if (timeBetweenPackets >= 0)
			        						Thread.sleep(timeBetweenPackets+1);
			        				}
			        			}
			        		}
			        		//Thread.sleep(10);
			        		if (i < numberOfTests) {
			        			Thread.sleep(timeBetweenTests);
			        		}
		            	}
		        		Thread.sleep(100);
		        		Runtime.getRuntime().exec("sleep 1 && su && killall tcpdump-coap");
		        		FileSender.sendFile(uri, "/storage/sdcard0/Download/" + testToken + ".pcap", testToken);
					}
				}
        		catch (InterruptedException e1) {}
				catch (IllegalStateException e) {}
        		catch (IOException e) {}
            }
		};
		new Thread( testrun ).start();
	}
}
