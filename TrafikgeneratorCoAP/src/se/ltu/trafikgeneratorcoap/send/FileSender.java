package se.ltu.trafikgeneratorcoap.send;

import java.io.File;
import java.io.FileInputStream;

import android.os.Environment;
import ch.ethz.inf.vs.californium.coap.Request;

public class FileSender {
	static public void sendFile(String uri, String logfile, String token) {
		Request controlMessage = Request.newPost();
		controlMessage.setURI("coap://" + uri + "/control?token=" + token);
		FileInputStream fileInputStream = null;
        File file = new File(logfile);
        byte[] bFile = new byte[(int) file.length()];
        try {
        	fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    
        } catch(Exception e) {;}
		controlMessage.setPayload(bFile);
		controlMessage.send();
	}
	static public boolean sendLog(String uri, String token) {
		String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
				"trafikgeneratorcoap" + File.separator + "logs" + File.separator + token + ".pcap";
		sendFile(uri, file, token);
		return true;
	}
}
