package se.ltu.trafikgeneratorcoap.testing;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class Logfile {
	private static String tcpdump = "tcpdump-coap", tcpdumpPath = "/data/local/";
	private static int packetCutoff = 84, tcpdumpPrepareTime = 5000;
	private static File logDirectory = new File(new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap"), "logs");
	private int port;
	private String timestamp, token;
	private File logfile;
	Logfile(TrafficConfig config, String timestamp, String token) {
		this.port = config.getIntegerSetting(Settings.TEST_TESTPORT);
		this.timestamp = timestamp;
		this.token = token;
	}
	boolean startLogging() throws IOException, InterruptedException {
		if (logfile != null)
			return false;
		logfile = new File(logDirectory, timestamp + "-" + token + "-sndr.pcap");
		logfile.getParentFile().mkdirs();
		if (!logfile.exists()) {
			String command = String.format("su ; %1$s -s %2$d -w %3$s 'port %4$d'", (tcpdumpPath+tcpdump), packetCutoff, logfile.toString(), port);
			Runtime.getRuntime().exec(command);
			Thread.sleep(tcpdumpPrepareTime);
			return true;
		}
		else
			return false;
	}
	void stopAllLogging() throws InterruptedException, IOException {
		Runtime.getRuntime().exec("su ; killall -s SIGINT " + tcpdump).waitFor();
		Thread.sleep(tcpdumpPrepareTime);
	}
}
