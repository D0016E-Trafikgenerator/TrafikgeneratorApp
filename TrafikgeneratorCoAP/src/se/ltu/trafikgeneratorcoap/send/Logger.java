package se.ltu.trafikgeneratorcoap.send;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class Logger {
	//TODO: Allow for own supplied tcpdump.
	static public boolean start(String token, Integer port) {
		String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
				"trafikgeneratorcoap" + File.separator + "logs" + File.separator + token + ".pcap";
		String command = "su && tcpdump-coap -s 65535 -w " + file + " 'port " + port + "' &";
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			return false;
		}
		if ((new File(file)).exists())
			return true;
		return false;
	}
	static public boolean stop() {
		try {
			Runtime.getRuntime().exec("sleep 1 && su && killall tcpdump-coap");
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
