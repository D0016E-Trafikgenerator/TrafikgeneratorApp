package se.ltu.trafikgeneratorcoap.send;

import java.io.File;
import java.io.FileInputStream;

import android.os.Environment;
import android.util.Log;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.coap.Response;

public class FileSender {
	static public boolean sendFile(String uri, File file, String query) {
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
		try {
			Response response = controlMessage.waitForResponse();
<<<<<<< Upstream, based on origin/GUI
=======
			Log.d("dummycoap", response.toString());
>>>>>>> e8c3075 1.3 works, probably
			return response.getCode().equals(ResponseCode.VALID);
		} catch (InterruptedException e) {
			return false;
		}
	}
	static public boolean sendLog(String uri, String token, String date) {
		File appRoot = new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap");
		File subDir = new File(appRoot, "logs");
		File file = new File(subDir, date + "-" + token + "-sndr.pcap");
		if (!file.exists())
			return false;
		return sendFile(uri, file, "?type=log&token=" + token + "&time=" + date);
	}
	static public boolean sendMeta(String uri, String token, String date) {
		File appRoot = new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap");
		File subDir = new File(appRoot, "logs");
		File file = new File(subDir, date + "-" + token + "-meta.txt");
		if (!file.exists())
			return false;
		return sendFile(uri, file, "?type=meta&token=" + token + "&time=" + date);
	}
}
