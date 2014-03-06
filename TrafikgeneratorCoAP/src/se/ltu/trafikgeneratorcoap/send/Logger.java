package se.ltu.trafikgeneratorcoap.send;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class Logger {
	static Process process;
	//TODO: Allow for user supplied tcpdump binary
	static public boolean start(String token, Integer port) {
		File appRoot = new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap");
		File subDir = new File(appRoot, "logs");
		File file = new File(subDir, (new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())).format(new Date()) + "-" + token + "-sndr.pcap");
		file.getParentFile().mkdirs();
		if (!file.exists()) {
			String command = "su ; tcpdump-coap -s 65535 -w " + file.toString() + " 'port " + port + "'";
			try {
				process = Runtime.getRuntime().exec("su ; echo \"hej\"");
				process.waitFor();
				process = Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				return false;
			} catch (InterruptedException e) {
				return false;
			}
		}
		return true;
	}
	static public boolean stop() {
		try {
			process = Runtime.getRuntime().exec("su ; killall -s SIGINT tcpdump-coap");
		} catch (IOException e) {
			return false;
		}
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}
}
