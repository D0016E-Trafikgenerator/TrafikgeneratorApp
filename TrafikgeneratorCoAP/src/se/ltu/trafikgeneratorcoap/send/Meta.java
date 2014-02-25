package se.ltu.trafikgeneratorcoap.send;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class Meta {
	static int gsmSignalStrength = 99;
	static private boolean writeToFile(String when, String config, String token, String date, String ntpServer, Long ntpError) {
		File appRoot = new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap");
		File subDir = new File(appRoot, "logs");
		File file = new File(subDir, date + "-" + token + "-meta.txt");
		if ((when.equals("BEFORE") && file.exists()) || (when.equals("AFTER") && !file.exists()))
			return false;
		try {
			Context context = Sending.context;
			PhoneStateListener listener = new PhoneStateListener() {
				public void onSignalStrengthsChanged(SignalStrength signalStrength){
					Meta.gsmSignalStrength = signalStrength.getGsmSignalStrength();
				}
			};
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			manager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			String networkType;
			switch(manager.getNetworkType()){
				case 0: networkType = "UNKNOWN"; break;
				case 1: networkType = "GPRS"; break;
				case 2: networkType = "EDGE"; break;
				case 3: networkType = "UMTS"; break;
				case 4: networkType = "CDMA"; break;
				case 5: networkType = "EVDO_0"; break;
				case 6: networkType = "EVDO_A"; break;
				case 7: networkType = "1xRTT"; break;
				case 8: networkType = "HSDPA"; break;
				case 9: networkType = "HSUPA"; break;
				case 10: networkType = "HSPA"; break;
				case 11: networkType = "iDen"; break;
				case 12: networkType = "EVDO_B"; break;
				case 13: networkType = "LTE"; break;
				case 14: networkType = "eHRPD"; break;
				case 15: networkType = "HSPA+"; break;
				default: networkType = "UNKNOWN";
			}
			if (when.equals("BEFORE"))
				file.getParentFile().mkdirs();
			BufferedWriter buf = new BufferedWriter(new FileWriter(file));

			if (when.equals("BEFORE")) {
				buf.write("DATETIME=" + date);
				buf.newLine();
				buf.write(TrafficConfig.configToTrimmedString(config));
				buf.newLine();
			}
			
			buf.write(when + "_TEST NTP_SERVER=" + ntpServer);
			buf.newLine();
			buf.write(when + "_TEST NTP_ERROR=" + ntpError);
			buf.newLine();
			buf.write(when + "_TEST NETWORK_TYPE=" + networkType);
			buf.newLine();
			buf.write(when + "_TEST SIGNAL_STRENGTH=" + Meta.gsmSignalStrength);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	static public boolean beforeTest(String config, String token, String date, String ntpServer, Long ntpError) {
		return writeToFile("BEFORE", config, token, date, ntpServer, ntpError);
	}
	static public boolean afterTest(String token, String date, String ntpServer, Long ntpError) {
		return writeToFile("AFTER", null, token, date, ntpServer, ntpError);
	}
}
