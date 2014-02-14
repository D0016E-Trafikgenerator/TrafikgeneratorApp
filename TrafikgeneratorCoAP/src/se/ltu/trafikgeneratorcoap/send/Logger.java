package se.ltu.trafikgeneratorcoap.send;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.util.Log;

public class Logger {
	private PrintWriter writeMe;
	
	public Logger(TrafficConfig config, String filename) {
		try {
			writeMe = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			log_config(config);
		} catch (IOException e) {
			;
		}
	}
	private void log_config(TrafficConfig config) {
		writeMe.print("PORT:" + config.getIntegerSetting(Settings.TEST_TESTPORT) + "\t");
		writeMe.print("ACK_TIMEOUT:" + config.getIntegerSetting(Settings.COAP_ACK_TIMEOUT) + "\t");
		writeMe.print("ACK_RANDOM_FACTOR:" + config.getDecimalSetting(Settings.COAP_ACK_RANDOM_FACTOR) + "\t");
		writeMe.print("MAX_RETRANSMIT:" + config.getIntegerSetting(Settings.COAP_MAX_RETRANSMIT) + "\t");
		writeMe.print("NSTART:" + config.getIntegerSetting(Settings.COAP_NSTART) + "\t");
		writeMe.print("PROBING_RATE:" + config.getIntegerSetting(Settings.COAP_PROBING_RATE) + "\t");
		writeMe.print("TYPE:" + config.getStringSetting(Settings.COAP_MESSAGETYPE) + "\t");
		writeMe.print("LOGGER: app\t");
		writeMe.println("TARGET_IP:" + config.getStringSetting(Settings.TEST_SERVER) + "\t");
	}
	public void log(Long time, String event, Integer messageID, String messageType, Integer payloadSize, Integer code, String token) {
		String poo = String.format("%d   %s   %d   %s   %d   %d   %s", time, "CLIENT_SENT", messageID, messageType, payloadSize, code, token);
		writeMe.println(poo);
		Log.i("dummycoap", poo);
		return;
	}
	public void flush() {
		writeMe.flush();
	}
	public void close() {
		writeMe.close();
	}
}
