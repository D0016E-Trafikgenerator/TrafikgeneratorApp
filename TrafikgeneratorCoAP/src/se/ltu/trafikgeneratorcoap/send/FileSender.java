package se.ltu.trafikgeneratorcoap.send;

import java.io.File;
import java.io.FileInputStream;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionSet;
import ch.ethz.inf.vs.californium.coap.Request;

public class FileSender {
	static public void sendFile(String uri, String logfile, String token) {
		Request controlMessage = Request.newPost();
		controlMessage.setURI("coap://" + uri + "/control?token=" + token);
		FileInputStream fileInputStream=null;
        File file = new File(logfile);
        byte[] bFile = new byte[(int) file.length()];
        try {
        	fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    
        } catch(Exception e) {;}
		OptionSet testServerOptions = controlMessage.getOptions();
		Option controlStopOption = new Option();
		controlStopOption.setNumber(65009);
		testServerOptions.addOption(controlStopOption);
		controlMessage.setOptions(testServerOptions);
		controlMessage.setPayload(bFile);
		controlMessage.send();
	}
}
