package se.ltu.trafikgeneratorcoap.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.os.Environment;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.coap.Response;

public class FileSender {
	private static File logDirectory = new File(new File(Environment.getExternalStorageDirectory(), "trafikgeneratorcoap"), "logs");
	static public boolean sendMetafile(String host, String token, String timestamp) throws IOException, InterruptedException {
		File metafile = new File(logDirectory, timestamp + "-" + token + "-meta.txt");
		if (!metafile.exists())
			return false;
		else
			return sendFile(host, metafile, "?type=meta&token=" + token + "&time=" + timestamp);
	}
	static public boolean sendLogfile(String host, String token, String timestamp) throws IOException, InterruptedException {
		File logfile = new File(logDirectory, timestamp + "-" + token + "-sndr.pcap");
		if (!logfile.exists())
			return false;
		else
			return sendFile(host, logfile, "?type=log&token=" + token + "&time=" + timestamp);
	}	
	static private boolean sendFile(String host, File file, String query) throws IOException, InterruptedException {
		Request controlMessage = Request.newPost();
		controlMessage.setURI(String.format("coap://%1$s/file%2$s", host, query));
		FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
    	fileInputStream = new FileInputStream(file);
	    fileInputStream.read(bFile);
	    fileInputStream.close();
		controlMessage.setPayload(bFile);
		controlMessage.send();
		Response response = controlMessage.waitForResponse();
		return response.getCode().equals(ResponseCode.VALID);
	}
}
