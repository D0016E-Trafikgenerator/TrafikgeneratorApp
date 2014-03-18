package se.ltu.trafikgeneratorcoap.testing;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import android.os.Environment;

public class Logfile {
	private static String tcpdump = "tcpdump-coap", tcpdumpPath = "/data/local/", tcpdumpInterface = "any";
	private static int packetCutoff = 84, tcpdumpPrepareTime = 5000;
	private static File logDirectory = new File(new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap"), "logs");
	private int port;
	private String timestamp, token, type;
	private File logfile;
	Logfile(Tester.Xfer type, TrafficConfig config, String timestamp, String token) {
		this.type = type.equals(Tester.Xfer.SEND)?"sndr":"rcvr";
		this.port = config.getIntegerSetting(Settings.TEST_TESTPORT);
		this.timestamp = timestamp;
		this.token = token;
	}
	boolean startLogging() throws IOException, InterruptedException {
		if (logfile != null)
			return false;
		logfile = new File(logDirectory, timestamp + "-" + token + "-" + type + ".pcap");
		logfile.getParentFile().mkdirs();
		if (!logfile.exists()) {
			String command = String.format(Locale.ROOT, "su ; %1$s -i %2$s -s %3$d -w %4$s 'port %5$d'", (tcpdumpPath+tcpdump), tcpdumpInterface, packetCutoff, logfile.toString(), port);
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
