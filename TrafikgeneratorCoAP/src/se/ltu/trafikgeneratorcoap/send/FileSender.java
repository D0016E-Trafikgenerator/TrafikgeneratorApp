package se.ltu.trafikgeneratorcoap.send;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;
import ch.ethz.inf.vs.californium.coap.Request;

public class FileSender {
	static public void sendFile(String uri, File file, String query) {
		Request controlMessage = Request.newPost();
		controlMessage.setURI("coap://" + uri + "/file" + query);
		FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
        	fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    
        } catch(Exception e) {
        	;
        }
		controlMessage.setPayload(bFile);
		controlMessage.send();
	}
	static public boolean sendLog(String uri, String token) {
		File appRoot = new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap");
		File subDir = new File(appRoot, "logs");
		File file = new File(subDir, (new SimpleDateFormat("yyyyMMdd", Locale.getDefault())).format(new Date()) + "-" + token + "-sndr.pcap");
		if (!file.exists())
			return false;
		sendFile(uri, file, "?token=" + token);
		return true;
	}
}
