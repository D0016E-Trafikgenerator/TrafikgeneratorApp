package se.ltu.trafikgeneratorcoap.send;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class Logger {
	//TODO: Allow for user supplied tcpdump binary
	static public boolean start(String token, Integer port) {
		File appRoot = new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap");
		File subDir = new File(appRoot, "logs");
		File file = new File(subDir, (new SimpleDateFormat("yyyyMMdd", Locale.getDefault())).format(new Date()) + token + "-sndr.pcap");
		file.getParentFile().mkdirs();
		if (!file.exists()) {
			String command = "su && tcpdump-coap -s 65535 -w " + file.toString() + " 'port " + port + "' &";
			try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				return false;
			}			
		}
		return true;
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
